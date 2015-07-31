package interfaces;

import java.util.Observable;

/**
 * @class IObserver
 * @desc Observer interface
 */

public interface IObserver {
    public void update(Observable e, Object t);
}
