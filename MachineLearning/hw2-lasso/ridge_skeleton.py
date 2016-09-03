import numpy
from scipy.optimize import minimize
from comtypes.npsupport import numpy
from sympy.mpmath.functions import theta
import copy
import matplotlib.pyplot as plt
import math
import time

def createdataset(m,d):
    X = numpy.random.rand(m,d)
    theta = numpy.zeros(d)
    for i in range(0,10):
        if(numpy.random.random_integers(-10,10) >0):
            theta[i] = 10
        else:
            theta[i] = -10
    epis = 0.1*numpy.random.randn(m)
    y = numpy.dot(X, theta) + epis    
    return X, y


def ridge(Lambda):
  def ridge_obj(theta_value):
    return ((numpy.linalg.norm(numpy.dot(X_train,theta_value) - y_train))**2)/(2*X_train.shape[0]) + Lambda*(numpy.linalg.norm(theta_value))**2
  return ridge_obj

def lasso(Lambda):
  def ridge_obj(theta_value):
    oneT = numpy.ones(theta_value.shape[0])  
    return ((numpy.linalg.norm(numpy.dot(X_train,theta_value) - y_train))**2)/(2*X_train.shape[0]) + (Lambda * numpy.dot(oneT, numpy.fabs(theta_value)))
  return ridge_obj

def compute_validation_loss(Lambda, theta_value):
  return ((numpy.linalg.norm(numpy.dot(X_test,theta_value) - y_test))**2)/(2*X_test.shape[0])

def compute_training_loss(Lambda, theta_value):
  return ((numpy.linalg.norm(numpy.dot(X_train,theta_value) - y_train))**2)/(2*X_train.shape[0])

def compute_lasso_shooting_algorithm_vectorized(X,y, thet, lambda_reg, num_iter = 1000):
    theta = copy.deepcopy(thet)
    (num_instances, num_features) = X.shape
    theta_hist = numpy.zeros((num_iter, num_features))  #Initialize theta_hist
    loss_hist = numpy.zeros(num_iter) #Initialize loss_hist
    
    for i in range(0,num_iter):
        a = 0
        c = 0
        for j in range(0, num_features):
            a = 2 * numpy.sum(X[:,j]**2)
            c = 2 * numpy.dot(X[:,j],(y - numpy.dot(X, theta) + numpy.dot(theta[j], X[:, j])))
            if(c < -lambda_reg):
                theta[j] = (c + lambda_reg)/a
            elif(c > lambda_reg):
                theta[j] = (c - lambda_reg)/a
            else:
                theta[j] = 0
        theta_hist[i,:] = theta
        loss_hist[i] = compute_training_loss(lambda_reg, theta)
        if(i > 0 and numpy.abs(loss_hist[i] - loss_hist[i-1]) < 10**-4):
            break
    return theta_hist,loss_hist,i
    
    
    
def compute_lasso_shooting_algorithm(X,y, thet, lambda_reg, num_iter=1000):
    theta = copy.deepcopy(thet)
    (num_instances, num_features) = X.shape
    theta_hist = numpy.zeros((num_iter, num_features))  #Initialize theta_hist
    loss_hist = numpy.zeros(num_iter) #Initialize loss_hist
    for i in range(0,num_iter):
        for j in range(0, num_features):
            a=0
            c=0
            for k in range(0,num_instances):
                a = a + X[k][j] * X[k][j]
                c = c + (X[k][j] * (y[k] - numpy.dot(theta, X[k,:]) + theta[j]* X[k][j]))
            a = 2*a
            c = 2*c
            if(c < -lambda_reg):
                theta[j] = (c + lambda_reg)/a
            elif(c > lambda_reg):
                theta[j] = (c - lambda_reg)/a
            else:
                theta[j] = 0
                    
        theta_hist[i,:] = theta
        loss_hist[i] = compute_training_loss(lambda_reg, theta)
        if(i > 0 and numpy.abs(loss_hist[i] - loss_hist[i-1]) < 10**-4):
            break
    return theta_hist,loss_hist,i
    
numpy.random.seed(10)
X,y = createdataset(150, 75)
X_train, y_train = X[0:80,:],y[0:80] 
X_validation, y_validation = X[80:100,:],y[80:100]
X_test, y_test = X[100:,:],y[100:]

def main():
    #X = numpy.loadtxt("X.txt")
    #y = numpy.loadtxt("y.txt")
    (N,D) = X.shape
    
    w = numpy.random.rand(D,1)
    
    
    ridge_loss = numpy.zeros(20)
    lasso_loss = numpy.zeros(20)
    log_lambdas = numpy.zeros(20)
    steps_taken = numpy.zeros(20)
    
    #Ridge regression
    print('Computing ridge regression parameters for different values of lambda')
    
    i = 0
    for j in range(-15,5):
        Lambda = 10**j;
        log_lambdas[i] = j
        w_opt = minimize(ridge(Lambda), w)
        ridge_loss[i] = compute_validation_loss(Lambda, w_opt.x)
        i = i +1
       
    print('Plotting validation loss for different values of lambda')
    
    fig,ax = plt.subplots()
    ax.plot(log_lambdas,ridge_loss)
    plt.axis([-15,4,0,5])
    plt.xlabel('log(lambda)')
    plt.ylabel('ridge_loss')
    plt.show()
    
    
    w_opt = minimize(ridge(10**-10), w)
    print('Value of theta corresponding to the lambda(10**-10) with minimum validation loss')
    print(w_opt.x)
       
     
     
    print('Lasso regression starts here')
    print('Compute lasso regression parameters for different values of lambda')
    lasso_loss = numpy.zeros(12)
    log_lambdas = numpy.zeros(12)
    steps_taken = numpy.zeros(12)
     
    starttime = time.time()
    i = 0
    for j in range(-5,7):
        Lambda = 10**j;
        log_lambdas[i] = j
            
        theta2, loss2,last_iter = compute_lasso_shooting_algorithm_vectorized(X_train, y_train, w[:,0], Lambda, 500)
        lasso_loss[i] = compute_validation_loss(Lambda, theta2[last_iter,:])
        steps_taken[i] = last_iter
        i = i +1
    print("Time taken by normal solution without homotopy")
    print(time.time() - starttime)
     
    
    print('Plotting steps taken for different values of lambda without homotopy')
    fig,ax = plt.subplots()
    ax.plot(log_lambdas,steps_taken)
    plt.axis([-6,0,0,600])
    plt.xlabel('log(lambda)')
    plt.ylabel('steps taken to converge without homotopy')
    plt.show()
     
     
    print('Plotting validation loss for different values of lambda') 
    fig,ax = plt.subplots()
    ax.plot(log_lambdas,lasso_loss)
    plt.axis([-5,6,0,50])
    plt.xlabel('log(lambda)')
    plt.ylabel('lasso_loss')
    plt.show()
     
    
    theta2, loss2,last_iter = compute_lasso_shooting_algorithm(X_train, y_train, w[:,0], 10, 500)
    print('Value of theta corresponding to the lambda(10**-10) with minimum validation loss')
    print(theta2[last_iter,:])
     
     
    
    print('Lasso regression with homotopy method starts here')
    starttime = time.time()
    i = 0
    th = w[:,0]
    for j in range(-5,2):
        Lambda = 10**j;
        log_lambdas[i] = j
        theta2, loss2,last_iter = compute_lasso_shooting_algorithm_vectorized(X_train, y_train, th, Lambda, 500)
        #lasso_loss[i] = compute_validation_loss(Lambda, theta2[last_iter,:])
        steps_taken[i] = last_iter
        th = theta2[last_iter,:]
        i = i +1
    print("Time taken by homotopy solution")
    print(time.time() - starttime)
      
    print('Plotting steps taken for different values of lambda with homotopy')
    fig,ax = plt.subplots()
    ax.plot(log_lambdas,steps_taken)
    plt.axis([-6,0,0,300])
    plt.xlabel('log(lambda)')
    plt.ylabel('steps taken to converge with homotopy')
    plt.show()
    
    starttime = time.time()
    theta, loss,last_iter = compute_lasso_shooting_algorithm(X_train, y_train, w[:,0], 10, 500)
    print('Taken time to compute lasso without vectorization:')
    print(time.time()- starttime)
    
    starttime = time.time()
    theta, loss,last_iter = compute_lasso_shooting_algorithm_vectorized(X_train, y_train, w[:,0], 10, 500)
    print('Taken time to compute lasso with vectorization:')
    print(time.time()- starttime)
    
if __name__ == "__main__":
    main()