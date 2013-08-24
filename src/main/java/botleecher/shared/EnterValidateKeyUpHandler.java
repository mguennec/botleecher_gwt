package botleecher.shared;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 24/08/13
 * Time: 17:49
 * To change this template use File | Settings | File Templates.
 */
public class EnterValidateKeyUpHandler implements KeyUpHandler {

    private Component component;

    public EnterValidateKeyUpHandler(final Component component) {
        this.component = component;
    }

    /**
     * Called when KeyUpEvent is fired.
     *
     * @param event the {@link com.google.gwt.event.dom.client.KeyUpEvent} that was fired
     */
    @Override
    public void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            component.fireEvent(new SelectEvent());
        }
    }
}
