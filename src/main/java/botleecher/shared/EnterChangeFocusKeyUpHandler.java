package botleecher.shared;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FocusWidget;
import com.sencha.gxt.widget.core.client.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 24/08/13
 * Time: 17:49
 * To change this template use File | Settings | File Templates.
 */
public class EnterChangeFocusKeyUpHandler implements KeyUpHandler {

    private Object widget;

    public EnterChangeFocusKeyUpHandler(final Component widget) {
        this.widget = widget;
    }
    public EnterChangeFocusKeyUpHandler(final FocusWidget widget) {
        this.widget = widget;
    }

    /**
     * Called when KeyUpEvent is fired.
     *
     * @param event the {@link com.google.gwt.event.dom.client.KeyUpEvent} that was fired
     */
    @Override
    public void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            if (widget instanceof Component) {
                ((Component) widget).focus();
            } else if (widget instanceof FocusWidget) {
                ((FocusWidget) widget).setFocus(true);
            }
        }
    }
}
