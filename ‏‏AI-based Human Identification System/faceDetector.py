import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '4'

import time
import uuid
import cv2
import shutil

import tensorflow as tf
import json
import numpy as np
from matplotlib import pyplot as plt

import albumentations as alb

from tensorflow.python.keras.models import Model
from tensorflow.python.keras.layers import Input, Conv2D, Dense, GlobalMaxPooling2D
from keras.applications.vgg16 import VGG16

tf.compat.v1.logging.set_verbosity(tf.compat.v1.logging.ERROR)

# split data into train, test, and val
if not os.path.isdir(os.path.join('data2')):
    os.mkdir(os.path.join('data2'))
else:
    shutil.rmtree(os.path.join('data2'))
    os.mkdir(os.path.join('data2'))

unpartitioned = os.path.join('data2', 'unpartitioned')
partitioned = os.path.join('data2', 'partitioned')
augmentated = os.path.join('data2', 'augmentated')
os.mkdir(unpartitioned)
os.mkdir(partitioned)
os.mkdir(augmentated)

os.mkdir(os.path.join(unpartitioned, 'images'))
os.mkdir(os.path.join(unpartitioned, 'labels'))


for folder in ['train','test','val']:
    partition = os.path.join(partitioned, folder)
    os.mkdir(partition)
    for folder in ['images','labels']:
        os.mkdir(os.path.join(partition, folder))

for folder in ['train','test','val']:
    partition = os.path.join(augmentated, folder)
    os.mkdir(partition)
    for folder in ['images','labels']:
        os.mkdir(os.path.join(partition, folder))

for folders in os.listdir('data'):
    for filename in os.listdir(os.path.join('data', folders, 'images')):
        name = '{}.jpg'.format(uuid.uuid1())
        oldpath = os.path.join('data', folders, 'images', filename)
        shutil.copy(oldpath, os.path.join(unpartitioned, 'images', name))
        oldpath = os.path.join('data', folders, 'labels', f'{filename.split(".")[0]}.json')
        shutil.copy(oldpath, os.path.join(unpartitioned, 'labels', f'{name.split(".")[0]}.json'))


num_of_files = len(os.listdir(os.path.join(unpartitioned, 'images')))
i = 0
for filename in os.listdir(os.path.join(unpartitioned, 'images')):
    oldpath = os.path.join(unpartitioned, 'images', filename)
    if(i < num_of_files*0.7):
        shutil.copyfile(oldpath, os.path.join(partitioned, 'train', 'images', filename))
    elif(i < num_of_files*0.85):
        shutil.copyfile(oldpath, os.path.join(partitioned, 'test', 'images', filename))
    else:
        shutil.copyfile(oldpath, os.path.join(partitioned, 'val', 'images', filename))
    i += 1


# Move the matching labels
for folder in ['train','test','val']:
    for file in os.listdir(os.path.join(partitioned, folder, 'images')):
        filename = file.split('.')[0]+'.json'
        oldpath = os.path.join(unpartitioned, 'labels', filename)
        if os.path.exists(oldpath): 
            shutil.copyfile(oldpath, os.path.join(partitioned, folder,'labels',filename))


# Image Augmentation
augmentor = alb.Compose([alb.HorizontalFlip(p=0.5), 
                         alb.RandomBrightnessContrast(p=0.2),
                         alb.RandomGamma(p=0.2), 
                         alb.RGBShift(p=0.2), 
                         alb.VerticalFlip(p=0.5)], 
                          bbox_params=alb.BboxParams(format='albumentations', 
                                                  label_fields=['class_labels']))

for partition in ['train','test','val']: 
    for image in os.listdir(os.path.join(partitioned, partition, 'images')):
        img = cv2.imread(os.path.join(partitioned, partition, 'images', image))

        coords = [0,0,0.00001,0.00001]
        label_path = os.path.join(partitioned, partition, 'labels', f'{image.split(".")[0]}.json')
        if os.path.exists(label_path):
            with open(label_path, 'r') as f:
                label = json.load(f)

            coords[0] = label['shapes'][0]['points'][0][0]
            coords[1] = label['shapes'][0]['points'][0][1]
            coords[2] = label['shapes'][0]['points'][1][0]
            coords[3] = label['shapes'][0]['points'][1][1]
            coords = list(np.divide(coords, [img.shape[1], img.shape[0], img.shape[1], img.shape[0]]))

        try: 
            for x in range(60):
                augmented = augmentor(image=img, bboxes=[coords], class_labels=['face'])
                cv2.imwrite(os.path.join(augmentated, partition, 'images', f'{image.split(".")[0]}.{x}.jpg'), augmented['image'])

                annotation = {}
                annotation['image'] = image

                if os.path.exists(label_path):
                    if len(augmented['bboxes']) == 0: 
                        annotation['bbox'] = [0,0,0,0]
                        annotation['class'] = 0 
                    else: 
                        annotation['bbox'] = augmented['bboxes'][0]
                        annotation['class'] = 1
                else: 
                    annotation['bbox'] = [0,0,0,0]
                    annotation['class'] = 0 

                with open(os.path.join(augmentated, partition, 'labels', f'{image.split(".")[0]}.{x}.json'), 'w') as f:
                    json.dump(annotation, f)
        
        except Exception as e:
            print(e)

# Load Augmented Images
def load_image(x): 
    byte_img = tf.io.read_file(x)
    img = tf.io.decode_jpeg(byte_img)
    return img

train_images = tf.data.Dataset.list_files(os.path.join(augmentated, 'train', 'images', '*.jpg'), shuffle=False)
train_images = train_images.map(load_image)
train_images = train_images.map(lambda x: tf.image.resize(x, (120,120)))
train_images = train_images.map(lambda x: x/255)

test_images = tf.data.Dataset.list_files(os.path.join(augmentated, 'test', 'images', '*.jpg'), shuffle=False)
test_images = test_images.map(load_image)
test_images = test_images.map(lambda x: tf.image.resize(x, (120,120)))
test_images = test_images.map(lambda x: x/255)

val_images = tf.data.Dataset.list_files(os.path.join(augmentated, 'val', 'images', '*.jpg'), shuffle=False)
val_images = val_images.map(load_image)
val_images = val_images.map(lambda x: tf.image.resize(x, (120,120)))
val_images = val_images.map(lambda x: x/255)

# Load Labels
def load_labels(label_path):
    with open(label_path.numpy(), 'r', encoding = "utf-8") as f:
        label = json.load(f)
        
    return [label['class']], label['bbox']

train_labels = tf.data.Dataset.list_files(os.path.join(augmentated, 'train', 'labels', '*.json'), shuffle=False)
train_labels = train_labels.map(lambda x: tf.py_function(load_labels, [x], [tf.uint8, tf.float16]))

test_labels = tf.data.Dataset.list_files(os.path.join(augmentated, 'test', 'labels', '*.json'), shuffle=False)
test_labels = test_labels.map(lambda x: tf.py_function(load_labels, [x], [tf.uint8, tf.float16]))

val_labels = tf.data.Dataset.list_files(os.path.join(augmentated, 'val', 'labels', '*.json'), shuffle=False)
val_labels = val_labels.map(lambda x: tf.py_function(load_labels, [x], [tf.uint8, tf.float16]))

# Combine Label and Image
train = tf.data.Dataset.zip((train_images, train_labels))
train = train.shuffle(5000)
train = train.batch(8)
train = train.prefetch(4)

test = tf.data.Dataset.zip((test_images, test_labels))
test = test.shuffle(1300)
test = test.batch(8)
test = test.prefetch(4)

val = tf.data.Dataset.zip((val_images, val_labels))
val = val.shuffle(1000)
val = val.batch(8)
val = val.prefetch(4)

# Build instance of the Model
batches_per_epoch = len(train)
lr_decay = (1./0.75 -1)/batches_per_epoch
opt = tf.keras.optimizers.Adam(learning_rate=0.0001, weight_decay=lr_decay)

def build_model(): 
    input_layer = Input(shape=(120,120,3))
    
    vgg = VGG16(include_top=False)(input_layer)

    # Classification Model  
    f1 = GlobalMaxPooling2D()(vgg)
    class1 = Dense(2048, activation='relu')(f1)
    class2 = Dense(1, activation='sigmoid')(class1)
    
    # Bounding box model
    f2 = GlobalMaxPooling2D()(vgg)
    regress1 = Dense(2048, activation='relu')(f2)
    regress2 = Dense(4, activation='sigmoid')(regress1)
    
    facetracker = Model(inputs=input_layer, outputs=[class2, regress2])
    return facetracker

facetracker = build_model()

# Define Losses and Optimizers
def localization_loss(y_true, yhat):            
    delta_coord = tf.reduce_sum(tf.square(y_true[:,:2] - yhat[:,:2]))
                  
    h_true = y_true[:,3] - y_true[:,1]
    w_true = y_true[:,2] - y_true[:,0] 

    h_pred = yhat[:,3] - yhat[:,1] 
    w_pred = yhat[:,2] - yhat[:,0] 
    
    delta_size = tf.reduce_sum(tf.square(w_true - w_pred) + tf.square(h_true-h_pred))
    
    return delta_coord + delta_size

classloss = tf.keras.losses.BinaryCrossentropy()
regressloss = localization_loss

# Train the model
class faceDetector(Model): 
    def __init__(self,  **kwargs): 
        super().__init__(**kwargs)
        self.model = facetracker

    def compile(self, opt, classloss, localizationloss, **kwargs):
        super().compile(**kwargs)
        self.closs = classloss
        self.lloss = localizationloss
        self.opt = opt
    
    def train_step(self, batch, **kwargs): 
        
        X, y = batch
        
        with tf.GradientTape() as tape: 
            classes, coords = self.model(X, training=True)
            
            batch_classloss = self.closs(y[0], classes)
            batch_localizationloss = self.lloss(tf.cast(y[1], tf.float32), coords)
            
            total_loss = batch_localizationloss+0.5*batch_classloss
            
            grad = tape.gradient(total_loss, self.model.trainable_variables)
        
        opt.apply_gradients(zip(grad, self.model.trainable_variables))
        
        return {"total_loss":total_loss, "class_loss":batch_classloss, "regress_loss":batch_localizationloss}
    
    def test_step(self, batch, **kwargs): 
        X, y = batch
        
        classes, coords = self.model(X, training=False)
        
        batch_classloss = self.closs(y[0], classes)
        batch_localizationloss = self.lloss(tf.cast(y[1], tf.float32), coords)
        total_loss = batch_localizationloss+0.5*batch_classloss
        
        return {"total_loss":total_loss, "class_loss":batch_classloss, "regress_loss":batch_localizationloss}
        
    def call(self, X, **kwargs): 
        return self.model(X, **kwargs)

model = faceDetector()
model.compile(opt, classloss, regressloss)

#model = faceDetector()

##
#model.fit(train, epochs=10, validation_data=val, callbacks=[tensorboard_callback])
# model.save_weights('data\\Anuel markar\\model_weights.h5')

##
# model.built = True
# model.load_weights('data\\Anuel markar\\model_weights.h5')