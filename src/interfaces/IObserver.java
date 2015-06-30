package interfaces;

import java.util.Observable;

public interface IObserver {
    public void update(Observable e, Object t);
}
