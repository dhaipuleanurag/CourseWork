import pandas as pd
import numpy as np
import math
import matplotlib.pyplot as plt

from sklearn.tree import DecisionTreeClassifier

def compute_loss(y_test, y_predicted):
    if len(y_test) == len(y_predicted):
        i = 0
        count = 0
        while i < len(y_test):
            if (y_test[i] > 0 and y_predicted[i] > 0) or (y_test[i] <= 0 and y_predicted[i] <= 0):
                count = count + 1
            i = i + 1
        return (1 - (count * 1.0/len(y_test)))
    else:
        print('Error!')

def compute_err(y_test, y_predicted, weights):
    if len(y_test) == len(y_predicted):
        i = 0
        total = 0
        err_sum = 0
        while i < len(y_test):
            total = total + weights[i]
            if y_test[i] != y_predicted[i]:
                err_sum = err_sum + weights[i]
            i = i + 1
        return err_sum * 1.0/total
    else:
        print('Error!')

def compute_err_combined(X_test, y_test, final_prediction):
    i= 0
    pred = np.zeros(X_test.shape[0])
    while i<len(final_prediction):
        pred = pred + final_prediction[i][1] * final_prediction[i][0].predict(X_test)
        i = i + 1
    return compute_loss(y_test, pred)
#def update_weights(weights, X_train, y_train):


#Parameters
n_classes = 2
plot_colors = "br"
plot_step = 0.02

df = pd.read_csv('D:\Academics\Courses\MachineLearning\hw5-boosting\data\\banana_train.csv', delimiter=',')
X_train = df.values[:,1:]
y_train = df.values[:,0]
y_train = np.asarray([0 if (y_train[i] == -1) else 1 for i in range(len(y_train))])


df = pd.read_csv('D:\Academics\Courses\MachineLearning\hw5-boosting\data\\banana_test.csv', delimiter=',')
X_test = df.values[:,1:]
y_test = df.values[:,0]
y_test = np.asarray([(0 if (y_test[i] == -1) else 1) for i in range(len(y_test))])


# Load data


# Shuffle
idx = np.arange(X_train.shape[0])
np.random.seed(13)
np.random.shuffle(idx)
X_train = X_train[idx]
y_train = y_train[idx]

 # Standardize
mean = X_train.mean(axis=0)
std = X_train.std(axis=0)
X_train = (X_train - mean) / std
#
 # Train
clf = DecisionTreeClassifier(max_depth=10).fit(X_train, y_train)

 # Plot the decision boundary
plt.subplot(1, 1, 1)

x_min, x_max = X_train[:, 0].min() - 1, X_train[:, 0].max() + 1
y_min, y_max = X_train[:, 1].min() - 1, X_train[:, 1].max() + 1
xx, yy = np.meshgrid(np.arange(x_min, x_max, plot_step), np.arange(y_min, y_max, plot_step))

Z = clf.predict(np.c_[xx.ravel(), yy.ravel()])
Z = Z.reshape(xx.shape)
cs = plt.contourf(xx, yy, Z, cmap=plt.cm.Paired)

plt.xlabel('x')
plt.ylabel('y')
plt.axis("tight")

target = ['-1', '1']
# Plot the training points
for i, color in zip(range(n_classes), plot_colors):
    idx = np.where(y_train == i)
    plt.scatter(X_train[idx, 0], X_train[idx, 1], c=color, label=target[i],cmap=plt.cm.Paired)
plt.axis("tight")

plt.suptitle("Decision surface of a decision tree using paired features")
plt.legend()
plt.show()
x = 5
all_trainingloss = np.zeros(10)
all_validationloss = np.zeros(10)
x_values = np.zeros(10)
i = 1
while i < 10:
    clf = DecisionTreeClassifier(max_depth=i).fit(X_train, y_train)
    x_values[i] = i
    Z = clf.predict(X_test)
    all_validationloss[i] = compute_loss(y_test, Z)
    Z = clf.predict(X_train)
    all_trainingloss[i] = compute_loss(y_train, Z)
    i = i + 1



fig, ax = plt.subplots()
ax.plot(x_values, all_trainingloss, '-y', label='training loss')
ax.plot(x_values, all_validationloss, '-b',label='validation loss')
plt.legend(loc='upper left')
plt.axis([1, 9, 0, 100])
plt.xlabel('depth')
plt.ylabel('loss')
plt.show()

y_test = np.asarray([(-1 if (y_test[i] == 0) else 1) for i in range(len(y_test))])
y_train = np.asarray([-1 if (y_train[i] == 0) else 1 for i in range(len(y_train))])


final_prediction = []
total_train_input = X_train.shape[0]
total_test_input = X_test.shape[0]
weights = np.ones(total_train_input)/(X_train.shape[0] * 1.0)
y_final_train = np.zeros(total_train_input)
y_final_test = np.zeros(total_test_input)
loss_train = np.zeros(10)
loss_test = np.zeros(10)
x_values = np.zeros(10)
j = 0
while j < 10:
    clf = DecisionTreeClassifier(max_depth=3).fit(X_train, y_train, weights)
    Z_train = clf.predict(X_train)
    Z_test = clf.predict(X_test)
    err = compute_err(y_train, Z_train, weights)
    loss = compute_loss(Z_train, y_train)
    alpha = 0.5 * math.log((1-err)/err)
    for i in range(0, total_train_input):
        if(Z_train[i] != y_train[i]):
            weights[i] = weights[i] * np.exp(alpha)

    final_prediction.append([clf, alpha])
    y_final_train = y_final_train + alpha * Z_train
    y_final_test = y_final_test + alpha * Z_test

    loss_train[j] = compute_loss(y_final_train, y_train)
    loss_test[j] = compute_loss(y_final_test, y_test)
    x_values[j] = j

    #err_on_combined = compute_err_combined(X_test, y_test, final_prediction)
    j = j+1

fig, ax = plt.subplots()
ax.plot(x_values, loss_train, '-y', label='training loss')
ax.plot(x_values, loss_test, '-b', label='validation loss')
plt.legend(loc='upper left')
plt.axis([1, 9, 0, 0.5])
plt.xlabel('depth')
plt.ylabel('loss')
plt.show()
x =5