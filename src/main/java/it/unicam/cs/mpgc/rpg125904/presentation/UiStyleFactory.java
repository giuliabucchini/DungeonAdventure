package it.unicam.cs.mpgc.rpg125904.presentation;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UiStyleFactory {

    public static final Color GOLD = Color.web("#E6BD70");
    public static final Color CREAM = Color.web("#F5E8CF");
    public static final Color DARK = Color.rgb(22, 17, 13, 0.94);
    public static final Color BLUE = Color.web("#18364A");
    public static final Color BRONZE = Color.web("#79552C");

    public static Button createButton(String text, Color color) {
        return createButton(text, color, CREAM);
    }

    public static Button createButton(String text, Color color, Color textColor) {
        Button button = new Button(text);
        button.setFont(Font.font("Serif", FontWeight.BOLD, 13));
        button.setTextFill(textColor);
        button.setPadding(new Insets(9, 13, 9, 13));
        button.setBackground(new Background(
                new BackgroundFill(color, new CornerRadii(7), Insets.EMPTY)
        ));
        return button;
    }

    public static Label styledLabel(String text, double size, Color color, boolean bold) {
        Label label = new Label(text);
        label.setFont(
                bold ? Font.font("Serif", FontWeight.BOLD, size) : Font.font("Serif", size)
        );
        label.setTextFill(color);
        return label;
    }
}