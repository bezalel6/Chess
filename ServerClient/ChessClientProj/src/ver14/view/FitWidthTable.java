package ver14.view;

import org.jetbrains.annotations.NotNull;
import ver14.view.IconManager.Size;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;

/**
 * represents a table that can fit its elements.
 *
 * @author Bezalel Avrahami (bezalel3250@gmail.com)
 */
public class FitWidthTable extends JTable {
    /**
     * The constant minWidth.
     */
    private static final int minWidth = 30;

    /**
     * The Computed size.
     */
    private Size computedSize;

    /**
     * Instantiates a new Fit width table.
     *
     * @param rowData     the row data
     * @param columnNames the column names
     */
    public FitWidthTable(@NotNull Object[][] rowData, @NotNull Object[] columnNames) {
        super(rowData, columnNames);
    }

    /**
     * calculates the optimal size for the contents of this table
     */
    public void fit() {
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int h = 0, w = 0;
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
            w += width;

            columnModel.getColumn(column).setPreferredWidth(width);
        }
        h = getRowCount() * getRowHeight();
        computedSize = new Size(w, h);
        System.out.println("table computed size = " + computedSize);
    }

    /**
     * Calc width int.
     *
     * @param renderer the renderer
     * @param r        the r
     * @param c        the c
     * @param currentW the current w
     * @return the int
     */
    private int calcWidth(TableCellRenderer renderer, int r, int c, int currentW) {
        Component comp = prepareRenderer(renderer, r, c);
        return Math.max(comp.getPreferredSize().width + 1, currentW);
    }

    /**
     * Gets the computed size of the table.
     *
     * @return the computed size
     */
    public Size getComputedSize() {
        return computedSize;
    }
}
