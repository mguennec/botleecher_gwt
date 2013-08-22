package botleecher.client.component;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 03/08/13
 * Time: 23:12
 * To change this template use File | Settings | File Templates.
 */
public interface IFilter<T> {
    boolean isValid(T value, String filter);
}
