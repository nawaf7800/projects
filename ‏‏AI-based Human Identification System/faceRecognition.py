import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '4'

import cv2
import uuid
import numpy as np
from matplotlib import pyplot as plt
import shutil

from tensorflow.python.keras.models import Model
from tensorflow.python.keras.layers import Layer, Conv2D, Dense, MaxPooling2D, Input, Flatten
import tensorflow as tf
from PyInquirer import prompt

tf.compat.v1.logging.set_verbosity(tf.compat.v1.logging.ERROR)

if not os.path.isdir(os.path.join('data3')):
    os.mkdir(os.path.join('data3'))
else:
    shutil.rmtree(os.path.join('data3'))
    os.mkdir(os.path.join('data3'))

POS_PATH = os.path.join('data3', 'positive')
NEG_PATH = os.path.join('data3', 'negative')
ANC_PATH = os.path.join('data3', 'anchor')
os.mkdir(POS_PATH)
os.mkdir(NEG_PATH)
os.mkdir(ANC_PATH)

# Data Augmentation
def data_aug(img):
    data = []
    for i in range(9):
        img = tf.image.stateless_random_brightness(img, max_delta=0.02, seed=(1,2))
        img = tf.image.stateless_random_contrast(img, lower=0.6, upper=1, seed=(1,3))
        img = tf.image.stateless_random_flip_left_right(img, seed=(np.random.randint(100),np.random.randint(100)))
        img = tf.image.stateless_random_jpeg_quality(img, min_jpeg_quality=90, max_jpeg_quality=100, seed=(np.random.randint(100),np.random.randint(100)))
        img = tf.image.stateless_random_saturation(img, lower=0.9,upper=1, seed=(np.random.randint(100),np.random.randint(100)))
            
        data.append(img)
        
    return data

# Preprocess
def preprocess(img_path1, img_path2, label):
    
    img1 = tf.io.read_file(img_path1)
    img1 = tf.io.decode_jpeg(img1)
    img1 = tf.image.resize(img1, (105,105))
    img1 = img1 / 255.0

    img2 = tf.io.read_file(img_path2)
    img2 = tf.io.decode_jpeg(img2)
    img2 = tf.image.resize(img2, (105,105))
    img2 = img2 / 255.0

    return img1, img2, label

FOLDER = 0
num_of_files = 0
def setFolder(folderName):
    FOLDER = os.path.join('data', folderName)
    num_of_files = len(os.listdir(os.path.join(FOLDER, 'faceImages')))

    shutil.rmtree(POS_PATH)
    os.mkdir(POS_PATH)
    shutil.rmtree(ANC_PATH)
    os.mkdir(ANC_PATH)

    # Move the dataset to positive and anchor folder
    i = 0
    for filename in os.listdir(os.path.join(FOLDER, 'faceImages')):
        oldpath = os.path.join(FOLDER, 'faceImages', filename)
        if(i < num_of_files*0.3):
            shutil.copy(oldpath, POS_PATH)
        else:
            shutil.copy(oldpath, ANC_PATH)
        i += 1
    
    # Move the dataset to negative folder
    for file in os.listdir('lfw'):
        path = os.path.join('lfw', file)
        new_path = os.path.join(NEG_PATH, file)
        shutil.copy(path, new_path)


    for folder in os.listdir('data'):
        if(os.path.join('data', folder) == FOLDER):
            continue
        for file in os.listdir(os.path.join('data', folder, 'faceImages')):
            path = os.path.join('data', folder, 'faceImages', file)
            new_path = os.path.join(NEG_PATH, file)
            shutil.copy(path, new_path)

    # Data Augmentation
    for file in os.listdir(os.path.join(POS_PATH)):
        path = os.path.join(POS_PATH, file)
        img = cv2.imread(path)
        augmented_images = data_aug(img)
    
        for image in augmented_images:
            cv2.imwrite(os.path.join(POS_PATH, '{}.jpg'.format(uuid.uuid1())), image.numpy())
    
    # load the dataset
    datasetSize = len(os.listdir(ANC_PATH))
    anchor = tf.data.Dataset.list_files(ANC_PATH+'\*.jpg').take(datasetSize)
    positive = tf.data.Dataset.list_files(POS_PATH+'\*.jpg').take(datasetSize)
    negative = tf.data.Dataset.list_files(NEG_PATH+'\*.jpg').take(datasetSize)

    positives = tf.data.Dataset.zip((anchor, positive, tf.data.Dataset.from_tensor_slices(tf.ones(len(anchor)))))
    negatives = tf.data.Dataset.zip((anchor, negative, tf.data.Dataset.from_tensor_slices(tf.zeros(len(anchor)))))
    dataset = positives.concatenate(negatives)

    dataset = dataset.map(preprocess)
    dataset = dataset.cache()
    dataset = dataset.shuffle(buffer_size=10000)

    # Split the dataset
    global train_data
    global test_data
    train_data = dataset.take(round(len(dataset)*.7))
    train_data = train_data.batch(16)
    train_data = train_data.prefetch(8)

    test_data = dataset.skip(round(len(dataset)*.7))
    test_data = test_data.take(round(len(dataset)*.3))
    test_data = test_data.batch(16)
    test_data = test_data.prefetch(8)

def getFolder():
    return FOLDER

# Build Embedding Layer
def embedding_layer():
    inp = Input(shape=(105,105,3), name='input_image')
    
    out = Conv2D(64, (10,10), activation='relu')(inp)
    out = MaxPooling2D(64, (2,2), padding='same')(out)
    out = Conv2D(128, (7,7), activation='relu')(out)
    out = MaxPooling2D(64, (2,2), padding='same')(out)
    out = Conv2D(128, (4,4), activation='relu')(out)
    out = MaxPooling2D(64, (2,2), padding='same')(out)
    out = Conv2D(256, (4,4), activation='relu')(out)
    out = Flatten()(out)
    out = Dense(4096, activation='sigmoid')(out)
    
    return Model(inputs=[inp], outputs=[out], name='embedding')

embedding = embedding_layer()

class L1Distance(Layer):
    
    def __init__(self, **kwargs):
        super().__init__()
       
    def call(self, input_embedding, validation_embedding):
        return tf.math.abs(input_embedding - validation_embedding)

l1 = L1Distance()

def make_siamese_model(): 
    
    input_image = Input(name='input_img', shape=(105,105,3))
    
    validation_image = Input(name='validation_img', shape=(105,105,3))
    
    siamese_layer = L1Distance()
    siamese_layer._name = 'distance'
    distances = siamese_layer(embedding(input_image), embedding(validation_image))
    
    classifier = Dense(1, activation='sigmoid')(distances)
    
    return Model(inputs=[input_image, validation_image], outputs=classifier, name='SiameseNetwork')

siamese_model = make_siamese_model()

## Training
binary_cross_loss = tf.losses.BinaryCrossentropy()
opt = tf.keras.optimizers.Adam(0.0001)

@tf.function
def train_step(batch):
    
    with tf.GradientTape() as tape:     
        X = batch[:2]
        y = batch[2]
        
        yhat = siamese_model(X, training=True)
        loss = binary_cross_loss(y, yhat)
    print(loss)
        
    grad = tape.gradient(loss, siamese_model.trainable_variables)
    
    opt.apply_gradients(zip(grad, siamese_model.trainable_variables))
        
    return loss
from tensorflow.python.keras.metrics import Precision, Recall
def train(data, EPOCHS):
    for epoch in range(1, EPOCHS+1):
        print('\n Epoch {}/{}'.format(epoch, EPOCHS))
        progbar = tf.keras.utils.Progbar(len(data))
        
        r = Recall()
        p = Precision()
        
        for idx, batch in enumerate(data):
            loss = train_step(batch)
            yhat = siamese_model.predict(batch[:2])
            r.update_state(batch[2], yhat)
            p.update_state(batch[2], yhat) 
            progbar.update(idx+1)
        print(loss.numpy(), r.result().numpy(), p.result().numpy())

##
#EPOCHS = 10
#train(train_data, EPOCHS)
#siamese_model.save_weights('data\\Anuel markar\\siamesemodel.h5')

##
#siamese_model.built = True
#siamese_model.load_weights('data\\Anuel markar\\siamesemodel.h5')