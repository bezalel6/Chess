package ver12.view;

import org.jetbrains.annotations.NotNull;
import ver12.view.Dialog.ScrollableComponent;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class FitWidthTable extends JTable implements ScrollableComponent {
    private static final int minWidth = 30;

    public FitWidthTable(@NotNull Object[][] rowData, @NotNull Object[] columnNames) {
        super(rowData, columnNames);
    }

    /**
     * <a href="https://stackoverflow.com/questions/17627431/auto-resizing-the-jtable-column-widths#:~:text=public%20void%20resizeColumnWidth,column).setPreferredWidth(width)%3B%0A%20%20%20%20%7D%0A%7D">slightly modified version of</a>
     */
    public void fit() {
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        final TableColumnModel columnModel = getColumnModel();
        for (int column = 0; column < getColumnCount(); column++) {
//            getColumnModel().getColumn(column).getHeaderRenderer()
//            int headerW = getTableHeader().getHeaderRect(column).width;

            ;
            int headerW = 10 + getTableHeader().getFontMetrics(getTableHeader().getFont()).stringWidth(getTableHeader().getColumnModel().getColumn(column).getHeaderValue().toString());

//            int headerW = getColumnModel().getColumn(column).getPreferredWidth();
            int width = Math.max(minWidth, headerW); // Min width
            for (int row = 0; row < getRowCount(); row++) {
                TableCellRenderer renderer = getCellRenderer(row, column);
                width = calcWidth(renderer, row, column, width);
            }
            if (width > 300)
                width = 300;

            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

    private int calcWidth(TableCellRenderer renderer, int r, int c, int currentW) {
        Component comp = prepareRenderer(renderer, r, c);
        return Math.max(comp.getPreferredSize().width + 1, currentW);
    }
}
