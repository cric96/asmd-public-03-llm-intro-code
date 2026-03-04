package it.unibo.tictactoe.view;

import it.unibo.tictactoe.model.Board;
import it.unibo.tictactoe.model.Player;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import static it.unibo.tictactoe.model.GameConstants.BOARD_SIZE;

/**
 * Swing-based implementation of {@link TicTacToeView} and {@link BoardView}.
 * Displays a {@code BOARD_SIZE x BOARD_SIZE} grid of buttons.
 */
public final class SwingTicTacToeView implements TicTacToeView, BoardView {
    private static final int CELL_SIZE = 100;
    private static final int FONT_SIZE = 36;
    private static final String FRAME_TITLE = "Tic Tac Toe";
    private static final String GAME_OVER_TITLE = "Game Over";
    private final JFrame frame;
    private final List<List<JButton>> buttons;
    private volatile CellListener listener;

    /**
     * Creates and displays the Tic Tac Toe Swing UI.
     */
    public SwingTicTacToeView() {
        this.frame = new JFrame(FRAME_TITLE);
        this.buttons = createButtons();
        initializeFrame();
    }

    @Override
    public BoardView view() {
        return this;
    }

    @Override
    public void attachListener(CellListener listener) {
        this.listener = listener;
    }

    @Override
    public void renderBoard(Board currentBoard) {
        SwingUtilities.invokeLater(() ->
            IntStream.range(0, BOARD_SIZE).forEach(row ->
                IntStream.range(0, BOARD_SIZE).forEach(col -> {
                    JButton button = buttons.get(row).get(col);
                    String text = currentBoard.getCell(row, col)
                        .map(Player::name)
                        .orElse("");
                    button.setText(text);
                    button.setEnabled(currentBoard.getCell(row, col).isEmpty());
                })
            )
        );
    }

    /**
     * Shows a modal message dialog (e.g. for game-over announcements).
     *
     * @param message the message to display
     */
    public void showMessage(String message) {
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(
                frame,
                message,
                GAME_OVER_TITLE,
                JOptionPane.INFORMATION_MESSAGE
            )
        );
    }

    private List<List<JButton>> createButtons() {
        return IntStream.range(0, BOARD_SIZE)
            .mapToObj(row -> IntStream.range(0, BOARD_SIZE)
                .mapToObj(col -> createButton(row, col))
                .toList()
            ).toList();
    }

    private JButton createButton(int row, int col) {
        final JButton button = new JButton("");
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, FONT_SIZE));
        button.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        button.setFocusPainted(false);
        button.addActionListener(e -> notifyListener(row, col));
        return button;
    }

    private void notifyListener(int row, int col) {
        CellListener currentListener = this.listener;
        if (currentListener != null) {
            currentListener.onCellClicked(row, col);
        }
    }

    private void initializeFrame() {
        JPanel panel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        buttons.stream()
                .flatMap(Collection::stream)
                .forEach(panel::add);
        frame.add(panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
