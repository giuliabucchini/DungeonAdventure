package it.unicam.cs.mpgc.rpg125904.presentation;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class InspectionStyleFactory {

    public static Button createButton(
            String text,
            Color color,
            Color cream
    ) {
        Button button = new Button(text);

        button.setFont(
                Font.font("Serif", FontWeight.BOLD, 13)
        );

        button.setTextFill(cream);
        button.setPadding(new Insets(9, 13, 9, 13));

        button.setBackground(new Background(
                new BackgroundFill(
                        color,
                        new CornerRadii(7),
                        Insets.EMPTY
                )
        ));

        button.setCursor(Cursor.HAND);

        return button;
    }

    public static Label styledLabel(
            String text,
            double size,
            Color color,
            boolean bold
    ) {
        Label label = new Label(text);

        label.setFont(
                bold
                        ? Font.font("Serif", FontWeight.BOLD, size)
                        : Font.font("Serif", size)
        );

        label.setTextFill(color);

        return label;
    }
}