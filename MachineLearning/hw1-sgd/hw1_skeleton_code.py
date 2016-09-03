import pandas as pd
#import logging
import numpy as np
import sys
import scipy
import matplotlib.pyplot as plt
from sklearn.cross_validation import train_test_split
import sklearn
from comtypes.npsupport import numpy
from statsmodels.sandbox.regression.kernridgeregress_class import plt_closeall
import math
from bokeh.models.plots import Plot
import time

### Assignment Owner: Anurag Dhaipule

#######################################
####Q2.1: Normalization


def feature_normalization(train, test):
    """Rescale the data so that each feature in the training set is in
    the interval [0,1], and apply the same transformations to the test
    set, using the statistics computed on the training set.

    Args:
        train - training set, a 2D numpy array of size (num_instances, num_features)
        test  - test set, a 2D numpy array of size (num_instances, num_features)
    Returns:
        train_normalized - training set after normalization
        test_normalized  - test set after normalization

    """
    num_features = test.shape[1]
    
    
    
    statistics = numpy.zeros((2,num_features))
    for i in range(0, num_features):
        min_val = min(train[:,i])
        statistics[0][i] = min_val
        max_val = max(train[:,i])
        statistics[1][i] = max_val
        normalized_values = [(x-min_val)/(max_val-min_val) for x in train[:,i]]
        train[:,i] = normalized_values
        
    
    for i in range(0,num_features):
        min_val = statistics[0][i]
        max_value = statistics[1][i]
        normalized_values = [(x-min_val)/(max_val-min_val) for x in test[:,i]]
        test[:,i] = normalized_values
        
    return train,test
    # TODO


########################################
####Q2.2a: The square loss function

def compute_square_loss(X, y, theta):
    """
    Given a set of X, y, theta, compute the square loss for predicting y with X*theta
    
    Args:
        X - the feature vector, 2D numpy array of size (num_instances, num_features)
        y - the label vector, 1D numpy array of size (num_instances)
        theta - the parameter vector, 1D array of size (num_features)
    
    Returns:
        loss - the square loss, scalar
    """
    loss = 0.0 #initialize the square_loss
    num_instances = X.shape[0]
    
    temp = numpy.dot(X,theta) - y
    loss = numpy.dot(temp.T,temp) 
    loss = loss/(2.0*num_instances)
    return loss
    #TODO
    


########################################
###Q2.2b: compute the gradient of square loss function
def compute_square_loss_gradient(X, y, theta):
    """
    Compute gradient of the square loss (as defined in compute_square_loss), at the point theta.
    
    Args:
        X - the feature vector, 2D numpy array of size (num_instances, num_features)
        y - the label vector, 1D numpy array of size (num_instances)
        theta - the parameter vector, 1D numpy array of size (num_features)
    
    Returns:
        grad - gradient vector, 1D numpy array of size (num_features)
    """
    #TODO
    num_instances = X.shape[0]
    diff_part = numpy.dot(X, theta) - y
    grad = (numpy.dot(diff_part, X))/(num_instances * 1.0)
    return grad
        
###########################################
###Q2.3a: Gradient Checker
#Getting the gradient calculation correct is often the trickiest part
#of any gradient-based optimization algorithm.  Fortunately, it's very
#easy to check that the gradient calculation is correct using the
#definition of gradient.
#See http://ufldl.stanford.edu/wiki/index.php/Gradient_checking_and_advanced_optimization
def grad_checker(X, y, theta, epsilon=0.01, tolerance=1e-4): 
    """Implement Gradient Checker
    Check that the function compute_square_loss_gradient returns the
    correct gradient for the given X, y, and theta.

    Let d be the number of features. Here we numerically estimate the
    gradient by approximating the directional derivative in each of
    the d coordinate directions: 
    (e_1 = (1,0,0,...,0), e_2 = (0,1,0,...,0), ..., e_d = (0,...,0,1) 

    The approximation for the directional derivative of J at the point
    theta in the direction e_i is given by: 
    ( J(theta + epsilon * e_i) - J(theta - epsilon * e_i) ) / (2*epsilon).

    We then look at the Euclidean distance between the gradient
    computed using this approximation and the gradient computed by
    compute_square_loss_gradient(X, y, theta).  If the Euclidean
    distance exceeds tolerance, we say the gradient is incorrect.

    Args:
        X - the feature vector, 2D numpy array of size (num_instances, num_features)
        y - the label vector, 1D numpy array of size (num_instances)
        theta - the parameter vector, 1D numpy array of size (num_features)
        epsilon - the epsilon used in approximation
        tolerance - the tolerance error
    
    Return:
        A boolean value indicate whether the gradient is correct or not

    """
    true_gradient = compute_square_loss_gradient(X, y, theta) #the true gradient
    num_features = theta.shape[0]
    approx_grad = np.zeros(num_features) #Initialize the gradient we approximate
    e = np.zeros(num_features)
    for i in range(0, num_features):
        e[i] = 1
        approx_grad[i] = (compute_square_loss(X, y, theta + epsilon * e) - compute_square_loss(X, y, theta - epsilon * e))/(2.0 * epsilon) 
        e[i] = 0
    sum = 0
    for i in range(0, num_features):
        sum = sum + math.pow(true_gradient[i] - approx_grad[i], 2)
    if(math.sqrt(sum) > epsilon):
        return False    
    return True
        
    #TODO
    
#################################################
###Q2.3b: Generic Gradient Checker
def generic_gradient_checker(X, y, theta, objective_func, gradient_func, epsilon=0.01, tolerance=1e-4):
    """
    The functions takes objective_func and gradient_func as parameters. And check whether gradient_func(X, y, theta) returned
    the true gradient for objective_func(X, y, theta).
    Eg: In LSR, the objective_func = compute_square_loss, and gradient_func = compute_square_loss_gradient
    """
    #TODO


####################################
####Q2.4a: Batch Gradient Descent
def batch_grad_descent(X, y, alpha=0.1, num_iter=1000, check_gradient=False):
    """
    In this question you will implement batch gradient descent to
    minimize the square loss objective
    
    Args:
        X - the feature vector, 2D numpy array of size (num_instances, num_features)
        y - the label vector, 1D numpy array of size (num_instances)
        alpha - step size in gradient descent
        num_iter - number of iterations to run 
        check_gradient - a boolean value indicating whether checking the gradient when updating
        
    Returns:
        theta_hist - store the the history of parameter vector in iteration, 2D numpy array of size (num_iter+1, num_features) 
                    for instance, theta in iteration 0 should be theta_hist[0], theta in ieration (num_iter) is theta_hist[-1]
        loss_hist - the history of objective function vector, 1D numpy array of size (num_iter+1) 
    """
    num_instances, num_features = X.shape[0], X.shape[1]
    theta_hist = np.zeros((num_iter+1, num_features))  #Initialize theta_hist
    loss_hist = np.zeros(num_iter+1) #initialize loss_hist
    theta = np.ones(num_features) #initialize theta
    theta1 = theta
    
    
    theta_hist[0,:] = theta
    loss_hist[0] = compute_square_loss(X, y, theta)
    for i in range(0, num_iter):
        theta = theta - alpha * compute_square_loss_gradient(X, y, theta)
        theta_hist[i+1,:] = theta
        loss_hist[i+1] = compute_square_loss(X, y, theta)
        if(check_gradient):
            grad_checker(X, y, theta)
    return theta_hist,loss_hist
####################################
###Q2.4b: Implement backtracking line search in batch_gradient_descent
###Check http://en.wikipedia.org/wiki/Backtracking_line_search for details
#TODO
    


###################################################
###Q2.5a: Compute the gradient of Regularized Batch Gradient Descent
def compute_regularized_square_loss_gradient(X, y, theta, lambda_reg):
    """
    Compute the gradient of L2-regularized square loss function given X, y and theta
    
    Args:
        X - the feature vector, 2D numpy array of size (num_instances, num_features)
        y - the label vector, 1D numpy array of size (num_instances)
        theta - the parameter vector, 1D numpy array of size (num_features)
        lambda_reg - the regularization coefficient
    
    Returns:
        grad - gradient vector, 1D numpy array of size (num_features)
    """
    num_instances = X.shape[0]
    diff_part = numpy.dot(X, theta) - y
    grad = (numpy.dot(diff_part, X))/ (num_instances * 1.0) 
    grad = grad + (2 * lambda_reg * theta)
    return grad

###################################################
###Q2.5b: Batch Gradient Descent with regularization term
def regularized_grad_descent(X, y, alpha=0.1, lambda_reg=1, num_iter=1000):
    """
    Args:
        X - the feature vector, 2D numpy array of size (num_instances, num_features)
        y - the label vector, 1D numpy array of size (num_instances)
        alpha - step size in gradient descent
        lambda_reg - the regularization coefficient
        numIter - number of iterations to run 
        
    Returns:
        theta_hist - the history of parameter vector, 2D numpy array of size (num_iter+1, num_features) 
        loss_hist - the history of regularized loss value, 1D numpy array
    """
    (num_instances, num_features) = X.shape
    theta = np.ones(num_features) #Initialize theta
    theta_hist = np.zeros((num_iter+1, num_features))  #Initialize theta_hist
    loss_hist = np.zeros(num_iter+1) #Initialize loss_hist
    #TODO
    
    theta_hist[0,:] = theta
    loss_hist[0] = compute_regularized_loss(X, y, theta, lambda_reg)
    for i in range(0, num_iter):
        theta = theta - alpha * compute_regularized_square_loss_gradient(X, y, theta, lambda_reg)
        theta_hist[i+1,:] = theta
        loss_hist[i+1] = compute_regularized_loss(X, y, theta, lambda_reg)
    return theta_hist,loss_hist

def compute_regularized_loss(X, y, theta, lambda_reg = 1):
    loss = 0.0 #initialize the square_loss
    num_instances = X.shape[0]
    temp = numpy.dot(X,theta) - y
    loss = numpy.dot(temp.T,temp)/(2.0*num_instances)  
    loss = loss + (lambda_reg * numpy.dot(theta, theta.T))
    return loss
    

#############################################
##Q2.5c: Visualization of Regularized Batch Gradient Descent
##X-axis: log(lambda_reg)
##Y-axis: square_loss
#Done in main function.

#############################################
###Q2.6a: Stochastic Gradient Descent
def stochastic_grad_descent(X, y, alpha=0.1, lambda_reg=1, num_iter=1000):
    """
    In this question you will implement stochastic gradient descent with a regularization term
    
    Args:
        X - the feature vector, 2D numpy array of size (num_instances, num_features)
        y - the label vector, 1D numpy array of size (num_instances)
        alpha - string or float. step size in gradient descent
                NOTE: In SGD, it's not always a good idea to use a fixed step size. Usually it's set to 1/sqrt(t) or 1/t
                if alpha is a float, then the step size in every iteration is alpha.
                if alpha == "1/sqrt(t)", alpha = 1/sqrt(t)
                if alpha == "1/t", alpha = 1/t
        lambda_reg - the regularization coefficient
        num_iter - number of epochs (i.e number of times) to go through the whole training set
    
    Returns:
        theta_hist - the history of parameter vector, 3D numpy array of size (num_iter, num_instances, num_features) 
        loss hist - the history of regularized loss function vector, 2D numpy array of size(num_iter, num_instances)
    """
    num_instances, num_features = X.shape[0], X.shape[1]
    theta = np.ones(num_features) #Initialize theta
    theta_hist = np.zeros((num_iter, num_instances, num_features))  #Initialize theta_hist
    loss_hist = np.zeros((num_iter, num_instances)) #Initialize loss_hist
    
    srot = (alpha == "1/sqrt(t)")
    inver = (alpha == "1/t")
    
    for j in range(0, num_iter):
        strt = time.time()
        if(srot):
            alpha = 0.01/math.sqrt(j+1)
        if(inver):
            alpha = 0.01/(j+1)
        
        for i in range(0, num_instances):
            input_value = X[i]
            if(j >0):
                theta = theta_hist[j-1,i,:]
            theta = theta - alpha * ((numpy.dot(input_value,theta) - y[i]) * input_value + 2 * lambda_reg * theta)
            theta_hist[j,i,:] = theta
            #loss_hist[j][i] = compute_stochastic_regularized_loss(X, y, theta, lambda_reg)
            loss_hist[j][i] = ((math.pow((numpy.dot(input_value, theta) - y[i]),2))/2.0) + (lambda_reg * numpy.dot(theta,theta.T))
    return theta_hist,loss_hist

################################################
###Q2.6b Visualization that compares the convergence speed of batch
###and stochastic gradient descent for various approaches to step_size
##X-axis: Step number (for gradient descent) or Epoch (for SGD)
##Y-axis: log(objective_function_value)
#This is done in main function


def main():
    #Loading the dataset
    print('loading the dataset')
    
    df = pd.read_csv('hw1-data.csv', delimiter=',')
    X = df.values[:,:-1]
    y = df.values[:,-1]

    print('Split into Train and Test')
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size =100, random_state=10)
    print("Scaling all to [0, 1]")
    X_train, X_test = feature_normalization(X_train, X_test)
    X_train = np.hstack((X_train, np.ones((X_train.shape[0], 1))))  # Add bias term
    X_test = np.hstack((X_test, np.ones((X_test.shape[0], 1)))) # Add bias term

    print('Running batch gradient descent for different step sizes')  
    #Batch gradient descent for different values of theta
    theta_hist1,loss_hist_withTwo = batch_grad_descent(X_train, y_train, 0.5)
    theta_hist3,loss_hist_withFifty = batch_grad_descent(X_train, y_train, 0.05)
    theta_hist4,loss_hist_withHundred = batch_grad_descent(X_train, y_train, 0.025)
    theta_hist5,loss_hist_5 = batch_grad_descent(X_train, y_train, 0.01)
    print('Plottin batch gradient descent')
    # Batch gradient descent for different values of theta plot
    t = np.arange(0,1001)
    fig,ax = plt.subplots()
    ax.plot(t,loss_hist_withTwo,'-y', label='alpha=0.5')
    ax.plot(t, loss_hist_withFifty,'-b',label='alpha =0.05')
    ax.plot(t, loss_hist_withHundred,'-r',label='alpha =0.025')
    ax.plot(t, loss_hist_5,'-g',label='alpha =0.01')
    ax.plot()
    plt.legend(loc='upper right')
    plt.axis([0,50,0,50])
    plt.xlabel('iterations')
    plt.ylabel('loss')
    plt.show()  
      
    print('Running ridge regression for different lambda values')  
    #Ridge regression for different lambda values
    all_log_lambda = numpy.zeros(100)
    all_lambda = numpy.zeros(100)
    all_trainingloss = numpy.zeros(100)
    all_validationloss = numpy.zeros(100)
    j = 1
    i = 0
    while j < 100:
        lam = j
        theta_his, loss_his = regularized_grad_descent(X_train, y_train, 0.01, lam)
        theta = theta_his[1000,:]
        all_lambda[i] = lam
        all_log_lambda[i] = math.log10(lam)
        all_validationloss[i] = compute_square_loss(X_test, y_test, theta)
        all_trainingloss[i] = compute_square_loss(X_train, y_train, theta)
        j = j+2
        i = i+1
    print('Plotting ridge regression for different values of lambda')    
    #Plot of ridge regression for different values of lambda 
    fig,ax = plt.subplots()
    ax.plot(all_log_lambda,all_trainingloss,'-y', label='training loss')
    ax.plot(all_log_lambda, all_validationloss,'-b',label='validation loss')
    plt.legend(loc='upper left')
    plt.axis([0,2,0,200])
    plt.xlabel('log(lambda)')
    plt.ylabel('loss')
    plt.show()  
          
    #theta that will be choosen after ridge regression
    theta,loss = regularized_grad_descent(X_train, y_train, 0.01, 10)
    choosentheta = theta[1000,:]
      
     
     
    #Stochastic gradient descent
    #Note that with fixed alpha size the function doesn't converge
    print('Running stochastic gradient descent for 1/t and 1/sqrt(t)')    
    theta_h, loss_h = stochastic_grad_descent(X_train, y_train, alpha=0.01, lambda_reg=10)    
    theta_ht1, loss_ht1 = stochastic_grad_descent(X_train, y_train, alpha="1/t", lambda_reg=10)
    theta_ht2, loss_ht2 = stochastic_grad_descent(X_train, y_train, alpha="1/sqrt(t)", lambda_reg=10)
    print('Plotting for stochastic gradient descent')
    t = np.arange(0,1000)
    fig,ax = plt.subplots()
    ax.plot(t,loss_ht1[:,1],'-b', label='alpha = 1/t')
    ax.plot(t,loss_ht2[:,1],'-r',label='alpha= 1/sqrt(t)')
    plt.legend(loc='upper right')
    plt.axis([0,50,0,200])
    plt.xlabel('epochs')
    plt.ylabel('loss')
    plt.show()
    
    print('Running and plotting for comparison of stochastic and batch gradient descent')
    #Stochastic gradient descent vs gradient descent
    theta_ht_stoc, loss_ht_stoc = stochastic_grad_descent(X_train, y_train, alpha="1/t", lambda_reg=10)
    theta_ht_grad, loss_ht_grad = batch_grad_descent(X_train, y_train, alpha=0.01)
    t = np.arange(0,1000)
    fig,ax = plt.subplots()
    ax.plot(t,loss_ht_stoc[:,1],'-b', label='stochastic')
    ax.plot(t,loss_ht_grad[0:-1],'-r',label='batch')
    plt.legend(loc='upper right')
    plt.axis([0,50,0,200])
    plt.xlabel('steps')
    plt.ylabel('loss')
    plt.show()

if __name__ == "__main__":
    main()
