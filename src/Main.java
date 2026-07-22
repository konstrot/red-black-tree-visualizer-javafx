import java.util.ArrayList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Didaktische Schritt-für-Schritt-Visualisierung eines Rot-Schwarz-Baums.
 */
public class Main extends Application {
    private static final int[] VALUES = {
        3, 5, 8, 10, 12, 15, 17, 20, 22, 25, 27, 30, 32, 35, 37
    };

    private final List<TreeStep> steps = new ArrayList<>();
    private final RBTreeVisualizer visualizer = new RBTreeVisualizer();

    private int currentStepIndex;
    private Timeline automaticPlayback;

    private Canvas canvas;
    private Label stepLabel;
    private Label actionLabel;
    private Label phaseLabel;
    private Label rootRuleLabel;
    private Label redRuleLabel;
    private Label blackHeightRuleLabel;
    private Button previousButton;
    private Button nextButton;
    private Button automaticButton;

    @Override
    public void start(Stage stage) {
        createSteps();

        canvas = new Canvas();
        StackPane canvasPane = new StackPane(canvas);
        canvasPane.setMinSize(0, 0);

        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

        canvas.widthProperty().addListener(
                (observable, oldValue, newValue) -> redrawCurrentStep());
        canvas.heightProperty().addListener(
                (observable, oldValue, newValue) -> redrawCurrentStep());

        Label titleLabel = new Label("Einfügen in einen Rot-Schwarz-Baum");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label sequenceLabel = new Label(
                "Aufsteigende Einfügefolge: 3, 5, 8, 10, 12, 15, 17, 20, "
                        + "22, 25, 27, 30, 32, 35, 37");
        sequenceLabel.setFont(Font.font("Arial", 14));
        sequenceLabel.setWrapText(true);

        stepLabel = new Label();
        stepLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        actionLabel = new Label();
        actionLabel.setWrapText(true);
        actionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        phaseLabel = new Label();
        phaseLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        rootRuleLabel = new Label();
        redRuleLabel = new Label();
        blackHeightRuleLabel = new Label();

        HBox ruleBox = new HBox(
                22,
                rootRuleLabel,
                redRuleLabel,
                blackHeightRuleLabel);
        ruleBox.setAlignment(Pos.CENTER);

        VBox informationBox = new VBox(
                7,
                titleLabel,
                sequenceLabel,
                stepLabel,
                actionLabel,
                phaseLabel,
                ruleBox);
        informationBox.setAlignment(Pos.CENTER);
        informationBox.setPadding(new Insets(14));
        informationBox.setStyle(
                "-fx-background-color: #f3f5f7;"
                        + "-fx-border-color: #c7ccd1;"
                        + "-fx-border-width: 0 0 1 0;");

        previousButton = new Button("Zurück");
        nextButton = new Button("Weiter");
        automaticButton = new Button("Automatisch");
        Button restartButton = new Button("Neu starten");

        previousButton.setOnAction(event -> showPreviousStep());
        nextButton.setOnAction(event -> showNextStep());
        automaticButton.setOnAction(event -> toggleAutomaticPlayback());
        restartButton.setOnAction(event -> restart());

        HBox controls = new HBox(
                12,
                previousButton,
                nextButton,
                automaticButton,
                restartButton);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(12));

        BorderPane rootPane = new BorderPane();
        rootPane.setMinSize(0, 0);
        rootPane.setTop(informationBox);
        rootPane.setCenter(canvasPane);
        rootPane.setBottom(controls);

        automaticPlayback = new Timeline(
                new KeyFrame(Duration.seconds(1.35), event -> showNextStep()));
        automaticPlayback.setCycleCount(Timeline.INDEFINITE);

        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double sceneWidth = Math.min(
                1200,
                Math.max(760, visualBounds.getWidth() - 80));
        double sceneHeight = Math.min(
                700,
                Math.max(480, visualBounds.getHeight() - 80));

        Scene scene = new Scene(rootPane, sceneWidth, sceneHeight);
        stage.setTitle("Rot-Schwarz-Baum – Schrittweise Visualisierung");
        stage.setScene(scene);
        stage.setMinWidth(Math.min(900, sceneWidth));
        stage.setMinHeight(Math.min(600, sceneHeight));
        stage.show();
        stage.centerOnScreen();

        showStep(0);
    }

    private void createSteps() {
        RBTree tree = new RBTree();

        steps.add(TreeStep.capture(
                tree,
                "Ausgangszustand: Der Baum ist leer."));

        tree.setStepListener((description, highlightedKeys) ->
                steps.add(TreeStep.capture(
                        tree,
                        description,
                        highlightedKeys)));

        for (int value : VALUES) {
            tree.insert(value);
        }
    }

    private void showStep(int index) {
        currentStepIndex = Math.max(0, Math.min(index, steps.size() - 1));
        TreeStep step = steps.get(currentStepIndex);

        visualizer.draw(canvas, step);

        stepLabel.setText(
                String.format(
                        "Schritt %d von %d",
                        currentStepIndex,
                        steps.size() - 1));
        actionLabel.setText(step.getDescription());

        if (step.isValidRedBlackTree()) {
            phaseLabel.setText("Gültiger Rot-Schwarz-Baum");
            phaseLabel.setStyle("-fx-text-fill: #146b2e;");
        } else {
            phaseLabel.setText("Zwischenzustand während der Reparatur");
            phaseLabel.setStyle("-fx-text-fill: #a13b00;");
        }

        setRuleStatus(
                rootRuleLabel,
                "Wurzel ist schwarz",
                step.isRootBlack());
        setRuleStatus(
                redRuleLabel,
                "Keine zwei roten Knoten hintereinander",
                step.hasNoRedRedViolation());

        String blackHeightText = step.hasEqualBlackHeight()
                ? "Gleiche Schwarzhöhe: " + step.getBlackHeight()
                : "Schwarzhöhen sind unterschiedlich";
        setRuleStatus(
                blackHeightRuleLabel,
                blackHeightText,
                step.hasEqualBlackHeight());

        previousButton.setDisable(currentStepIndex == 0);
        nextButton.setDisable(currentStepIndex == steps.size() - 1);

        if (currentStepIndex == steps.size() - 1) {
            stopAutomaticPlayback();
        }
    }

    private void redrawCurrentStep() {
        if (!steps.isEmpty()
                && canvas.getWidth() > 0
                && canvas.getHeight() > 0) {
            visualizer.draw(canvas, steps.get(currentStepIndex));
        }
    }

    private void setRuleStatus(Label label, String text, boolean valid) {
        label.setText((valid ? "✓ " : "✗ ") + text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        label.setStyle(valid
                ? "-fx-text-fill: #146b2e;"
                : "-fx-text-fill: #b3261e;");
    }

    private void showPreviousStep() {
        stopAutomaticPlayback();
        showStep(currentStepIndex - 1);
    }

    private void showNextStep() {
        if (currentStepIndex < steps.size() - 1) {
            showStep(currentStepIndex + 1);
        } else {
            stopAutomaticPlayback();
        }
    }

    private void toggleAutomaticPlayback() {
        if (automaticPlayback.getStatus() == Timeline.Status.RUNNING) {
            stopAutomaticPlayback();
        } else {
            if (currentStepIndex == steps.size() - 1) {
                showStep(0);
            }
            automaticButton.setText("Pause");
            automaticPlayback.play();
        }
    }

    private void stopAutomaticPlayback() {
        if (automaticPlayback != null) {
            automaticPlayback.stop();
        }
        if (automaticButton != null) {
            automaticButton.setText("Automatisch");
        }
    }

    private void restart() {
        stopAutomaticPlayback();
        showStep(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
