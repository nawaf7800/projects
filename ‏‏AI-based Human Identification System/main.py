import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '4'

from PyInquirer import prompt
import easygui
import cv2
import tensorflow as tf
from tensorflow.python.keras.metrics import Precision, Recall

import time
import uuid
import shutil
import matplotlib.image
import uuid
import numpy as np

import faceDetector
import faceRecognition

tf.compat.v1.logging.set_verbosity(tf.compat.v1.logging.ERROR)

mainMenu = ['New Record', 'Update Record', 'Delete Record', 'Model Training','Test', 'Exit']
records = os.listdir('data')
records.insert(0, 'Main Menu')
collectImgChoices = ["Open Folder", "Open Camera", "Label Images", "Main Menu"]

q1 = [
    {
        'type': 'list',
        'name': 'q1',
        'message': 'Select Action:',
        'choices': mainMenu
    }
]
q2 = [
    {
        'type': 'list',
        'name': 'q2',
        'message': 'Select Record:',
        'choices': records
    }
]
q3 = [
   {
        'type': 'list',
        'name': 'q3',
        'message': 'Collect Images:',
        'choices': collectImgChoices
    }
]
q4 = [
   {
        'type': 'input',
        'name': 'q4',
        'message': 'Enter the name of the record:',
    }
]



def saveImg(images, folder):
    for img in images:
        cv2.imwrite(os.path.join(folder, '{}.jpg'.format(uuid.uuid1())), img)

def load_images_from_folder(folder):
    images = []
    for filename in os.listdir(folder):
        img = cv2.imread(os.path.join(folder,filename))
        if img is not None:
            images.append(img)
    return images

def camera():
    cap = cv2.VideoCapture(0)
    i = 1
    images = []
    while cap.isOpened(): 
        print('Collecting image', i)
        i +=1
        ret, frame = cap.read()
        cv2.imshow('frame', frame)
        images.append(frame)
        time.sleep(0.5)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break
    cap.release()
    cv2.destroyAllWindows()
    return images

def imageCollector(folderName):
    dir = os.path.join('data', folderName)
    if not os.path.isdir(dir):
        os.makedirs(dir)
        os.makedirs(os.path.join(dir, 'images'))
        os.makedirs(os.path.join(dir, 'labels'))
        records.append(folderName)
    images = os.path.join(dir, 'images')
    
    while True:
        answers = prompt(q3)
        if(answers.get("q3") == collectImgChoices[0]):
            folder = easygui.diropenbox(title='Choose images folder')
            saveImg(load_images_from_folder(folder), images)
            
        elif(answers.get("q3") == collectImgChoices[1]):
            capturedImg = camera()
            saveImg(capturedImg, images)
        elif(answers.get("q3") == collectImgChoices[2]):
            os.system('labelme data/'+folderName+'/images --labels face --autosave --output data/'+folderName+'/labels/')
        else:
            break

def deleteRecord(folderName):
    shutil.rmtree(os.path.join('data', folderName))
    records.remove(folderName)


def CalculatePerformance(siamese_model, test_data):
    test_input, test_val, y_true = test_data.as_numpy_iterator().next()
    y_hat = siamese_model.predict([test_input, test_val])
    [1 if prediction > 0.5 else 0 for prediction in y_hat ]

    m = Recall()

    m.update_state(y_true, y_hat)

    m = Precision()

    m.update_state(y_true, y_hat)

    r = Recall()
    p = Precision()

    for test_input, test_val, y_true in test_data.as_numpy_iterator():
        yhat = siamese_model.predict([test_input, test_val])
        r.update_state(y_true, yhat)
        p.update_state(y_true,yhat) 

    print(r.result().numpy(), p.result().numpy())

def training():
    faceDetector.model.fit(faceDetector.train, epochs=10, validation_data=faceDetector.val)
    faceDetector.model.save_weights('faceDetector.h5')

    answers = prompt(q2)
    faceRecognition.setFolder(answers.get("q2"))

    faceRecognition.train(faceRecognition.train_data, 10)
    faceRecognition.siamese_model.save_weights(os.path.join(faceRecognition.getFolder(), 'faceRecognition.h5'))
    CalculatePerformance(faceRecognition.siamese_model, faceRecognition.test_data)

def test(FolderName):
    faceDetector.model.built = True
    faceDetector.model.load_weights('faceDetector.h5')

    faceImages = os.path.join('data', FolderName, 'faceImages')

    for folders in os.listdir('data'):
        if(len(os.listdir(os.path.join('data', folders, 'images'))) == 0): continue

        test_images = tf.data.Dataset.list_files(os.path.join('data', folders, 'images', '*.jpg'), shuffle=False)
        test_images = test_images.map(faceDetector.load_image)
        test_images = test_images.map(lambda x: tf.image.resize(x, (120,120)))
        test_images = test_images.map(lambda x: x/255)
        test_images = test_images.shuffle(1300)
        test_images = test_images.batch(8)
        test_images = test_images.prefetch(4)


        test_data = test_images.as_numpy_iterator()

        while True:
            test_sample = next(test_data, None)
            if(type(test_sample) == type(None)): break
            yhat = faceDetector.facetracker.predict(test_sample)

            for idx in range(len(test_sample)):
                sample_image = test_sample[idx]
                sample_coords = list(np.multiply(yhat[1][idx], [120,120,120,120]).astype(int))

                if yhat[0][idx] > 0.9:
                    sample_image = sample_image[sample_coords[0]:sample_coords[2], sample_coords[1]:sample_coords[3]]
                    matplotlib.image.imsave(os.path.join(faceImages, '{}.jpg'.format(uuid.uuid1())), sample_image)

    faceRecognition.setFolder(FolderName)
    faceRecognition.siamese_model.built = True
    faceRecognition.siamese_model.load_weights(os.path.join('data', FolderName, 'faceRecognition.h5'))
    CalculatePerformance(faceRecognition.siamese_model, faceRecognition.test_data)





if __name__ == "__main__":
    print("\r\n######################### AI-based Human Identification System #########################\r\n")

    while True:
        answers = prompt(q1)

        if answers.get("q1") == mainMenu[0]:
            answers = prompt(q4)
            imageCollector(answers.get("q4").strip())
        elif answers.get("q1") == mainMenu[1]:
            answers = prompt(q2)
            if not answers.get("q2") == records[0]:
                imageCollector(answers.get("q2"))
        elif answers.get("q1") == mainMenu[2]:
            answers = prompt(q2)
            if not answers.get("q2") == records[0]:
                deleteRecord(answers.get("q2"))
        elif answers.get("q1") == mainMenu[3]:
            training()
        elif answers.get("q1") == mainMenu[4]:
            if(not (os.path.isfile('faceDetector.h5'))):
                print('\r\nPlease train the model first. Select Model Training')
                continue
            for folder in os.listdir('data'):
                faceImages = os.path.join('data', folder, 'faceImages')
                if not os.path.isdir(faceImages):
                    os.mkdir(faceImages)
            flag = False
            for folder in os.listdir('data'):
                faceImages = os.path.join('data', folder, 'faceImages')
                if(os.path.isfile(os.path.join('data', folder, 'faceRecognition.h5'))):
                    test(folder)
                    flag = True
            if(not flag): print('\r\nPlease train the model first. Select Model Training')
        else:
            break
        print()