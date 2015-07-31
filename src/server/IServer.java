package server;


import interfaces.IObserver;

/**
 * @interface IServer
 * @desc IServer interface used by all of the different types of servers
 * LoadBalancerServer and ChatServer
 * All classes that implement this automatically become observers
 */

public interface IServer extends IObserver {
    public void startServer();
}
