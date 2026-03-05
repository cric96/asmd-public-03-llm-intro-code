package e1.view;

import e1.model.Player;

import java.util.List;
import java.util.Scanner;

public final class ConsoleStoryView implements StoryView {

    private final Scanner scanner;

    public ConsoleStoryView(Scanner scanner) {
        this.scanner = scanner;
    }

    public ConsoleStoryView() {
        this(new Scanner(System.in));
    }

    @Override
    public void showBeat(
        final String narrative,
        final Player player,
        final String question,
        final List<String> choices
    ) {
        System.out.println("\n" + "=".repeat(60));
        System.out.printf("[%s | HP: %d | ATK: %d]%n", player.name(), player.health(), player.attackPower());
        System.out.println("=".repeat(60));
        System.out.println(narrative);
        System.out.println();
        System.out.println(question);
        for (int i = 0; i < choices.size(); i++) {
            System.out.printf("  [%d] %s%n", i, choices.get(i));
        }
    }

    @Override
    public void showGameOver(Player player) {
        System.out.println("\n=== GAME OVER ===");
        System.out.printf("Final stats — %s: HP=%d, ATK=%d%n",
            player.name(), player.health(), player.attackPower());
    }

    @Override
    public int readChoice(int numChoices) {
        while (true) {
            System.out.print("\nYour choice: ");
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                if (choice >= 0 && choice < numChoices) return choice;
            } else {
                scanner.next();
            }
            System.out.printf("Please enter a number between 0 and %d%n", numChoices - 1);
        }
    }
}
