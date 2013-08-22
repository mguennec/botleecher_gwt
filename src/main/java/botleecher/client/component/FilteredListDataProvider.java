package botleecher.client.component;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 03/08/13
 * Time: 23:13
 * To change this template use File | Settings | File Templates.
 */
public class FilteredListDataProvider<T> extends ListDataProvider<T> {
    private String filterString;
    private final IFilter<T> filter;

    public FilteredListDataProvider(IFilter<T> filter) {
        this.filter = filter;
    }

    public String getFilter() {
        return filterString;
    }

    public void setFilter(final String filter) {
        this.filterString = filter;
        refresh();
    }

    public boolean hasFilter() {
        return filterString != null && !filterString.trim().isEmpty();
    }

    @Override
    protected void updateRowData(HasData<T> display, int start, List<T> values) {
        if (!hasFilter() || filter == null) { // we don't need to filter, so call base class
            display.setRowCount(values.size());
            super.updateRowData(display, start, values);
        } else {
            int end = start + values.size();
            List resulted = new ArrayList(values.size());
            for (int i = start; i < end; i++) {
                if (filter.isValid(values.get(i), getFilter())) {
                    resulted.add(values.get(i));
                }
            }
            display.setRowData(start, resulted);
            display.setRowCount(resulted.size());
        }
    }
}
