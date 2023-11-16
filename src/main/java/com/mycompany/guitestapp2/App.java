package com.mycompany.guitestapp2;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.stage.Stage;
import java.sql.*;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.imageio.ImageIO;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class App extends Application {
    
    int dbImageSize = 200;
    int subImageSize = 20;
    String standardDB = "images.db";

    @Override
    public void start(Stage stage) {
        stage.setTitle("Mosaic Creator");
        
        VBox vBox = new VBox();
            
        // MAIN STAGE
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        vBox.getChildren().add(grid);
        
        int index = 0;
        
        Label selectLabel = new Label("Select Image:");
        selectLabel.setAlignment(Pos.BASELINE_RIGHT);
        grid.add(selectLabel, 0, index++);
        
        Button selectBtn = new Button("Browse");
        TextField imageTextField = new TextField();
        imageTextField.setAlignment(Pos.BASELINE_LEFT);
        HBox selectHB = new HBox(10);
        selectHB.setAlignment(Pos.BASELINE_RIGHT);
        selectHB.getChildren().add(selectBtn);
        selectHB.getChildren().add(imageTextField);
        grid.add(selectHB, 0, index++);
        
        Label outputLabel = new Label("Select Output Folder:");
        outputLabel.setAlignment(Pos.BASELINE_RIGHT);
        grid.add(outputLabel, 0, index++);
        
        Button outputBtn = new Button("Browse");
        TextField outputTextField = new TextField();
        outputTextField.setText("C:\\Users\\BlifernM\\Desktop");
        outputTextField.setAlignment(Pos.BASELINE_LEFT);
        HBox outputHB = new HBox(10);
        outputHB.setAlignment(Pos.BASELINE_RIGHT);
        outputHB.getChildren().add(outputBtn);
        outputHB.getChildren().add(outputTextField);
        grid.add(outputHB, 0, index++);
        
        Label dbLabel = new Label("Select Database");
        ObservableList<String> dbOptions = FXCollections.observableArrayList();
        dbOptions.add(standardDB);
        ComboBox dbCombo = new ComboBox(dbOptions);
        dbCombo.getSelectionModel().selectFirst();
        grid.add(dbLabel, 0, index++);
        grid.add(dbCombo, 0, index++);
        
        Label nameLabel = new Label("Save Image As:");
        nameLabel.setAlignment(Pos.BASELINE_RIGHT);
        grid.add(nameLabel, 0, index++);
        
        TextField nameTextField = new TextField();
        nameTextField.setText("output");
        nameTextField.setAlignment(Pos.BASELINE_LEFT);
        grid.add(nameTextField, 0, index++);
        
        Label radioLabel = new Label("Set Size per Subimage:");
        radioLabel.setAlignment(Pos.BASELINE_RIGHT);
        grid.add(radioLabel, 0, index++);
        
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton radioBtn1 = new RadioButton("10");
        RadioButton radioBtn2 = new RadioButton("20");
        radioBtn1.setToggleGroup(toggleGroup);
        radioBtn2.setToggleGroup(toggleGroup);
        radioBtn2.setSelected(true);
        HBox radioHB = new HBox(10);
        radioHB.setAlignment(Pos.BASELINE_LEFT);
        radioHB.getChildren().add(radioBtn1);
        radioHB.getChildren().add(radioBtn2);
        grid.add(radioHB, 0, index++);
        
        Label createLabel = new Label("Create Mosaic:");
        createLabel.setAlignment(Pos.BASELINE_RIGHT);
        grid.add(createLabel, 0, index++);
        
        Button createBtn = new Button("Create");
        createBtn.setAlignment(Pos.BASELINE_RIGHT);
        grid.add(createBtn, 0, index++);
        
        ProgressBar progress = new ProgressBar();
        Label progressLabel = new Label("Image saved.");
        progressLabel.setTextFill(Color.GREEN);
        HBox progressHB = new HBox(10);
        progressHB.setAlignment(Pos.BASELINE_LEFT);
        progressHB.setMinHeight(20.0);
        grid.add(progressHB, 0, index++);

        selectBtn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent e) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select an Image");
                fileChooser.getExtensionFilters().addAll(
                        new ExtensionFilter("Image Files", "*.png", "*.jpg"));
                File selectedFile = fileChooser.showOpenDialog(stage);
                if (selectedFile != null) {
                    imageTextField.setText(selectedFile.getPath());
                }
            }
        });
        
        outputBtn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent e) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Select Output Folder");
                directoryChooser.setInitialDirectory(new File("C:\\Users\\BlifernM\\Desktop"));
                File selectedFile = directoryChooser.showDialog(stage);
                if (selectedFile != null) {
                    outputTextField.setText(selectedFile.getPath());
                }
            }
        });
        
        radioBtn1.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent e) {
                subImageSize = 10;
            }
        });
        
        radioBtn2.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent e) {
                subImageSize = 20;
            }
        });
        
        createBtn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent e) {
                CreateMosaicTask task = new CreateMosaicTask(
                        dbImageSize,
                        subImageSize,
                        imageTextField.getText(),
                        outputTextField.getText(),
                        nameTextField.getText(),
                        dbCombo.getValue().toString()
                );
                task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent t) {
                        progressHB.getChildren().remove(progress);
                        progressHB.getChildren().add(progressLabel);
                    }
                });
                
                progressHB.getChildren().add(progress);
                progressHB.getChildren().remove(progressLabel);
                progress.progressProperty().bind(task.progressProperty());
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
            }
        });
        
        Scene scene = new Scene(vBox, 300, 450);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
    
    private static class CreateMosaicTask extends Task<Void> {
        
        final private int dbImageSize;
        final private int subImageSize;
        
        final private String inputPath;
        final private String outputPath;
        final private String imageName;        
        final private String database;
        
        public CreateMosaicTask(int dbISize, int sISize, String input, String output, String name, String db) {
            this.dbImageSize = dbISize;
            this.subImageSize = sISize;
            this.inputPath = input;
            this.outputPath = output;
            this.imageName = name;
            this.database = db;
        }
        
        @Override
        protected Void call() throws Exception {
            BufferedImage resultImage = getMosaic(this.inputPath);
            String output = this.outputPath + "/" + this.imageName + ".jpg";
            saveImage(resultImage, output);
            
            return null;
        }
        
        public boolean saveImage(BufferedImage image, String outputPath) {
            File outputFile = new File(outputPath);

            try {
                ImageIO.write(image, "jpg", outputFile);

                return true;
            } catch (IOException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());

                return false;
            }
        }

        public BufferedImage getMosaic(String path) {
            BufferedImage image = null;
            BufferedImage resultImage = null;

            try {
                File file = new File(path);
                image = ImageIO.read(file.toURI().toURL());
            }
            catch (IOException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }

            if (image != null) {

                int widthScaled = getValidNumber(image.getWidth(), subImageSize);
                int heightScaled = getValidNumber(image.getHeight(), subImageSize);

                BufferedImage imageScaled = scaleImage(image, widthScaled, heightScaled);

                int widthFinalImage = widthScaled * (dbImageSize / subImageSize);
                int heightFinalImage = heightScaled * (dbImageSize / subImageSize);

                resultImage = createMosaic(imageScaled, widthFinalImage, heightFinalImage);
            }

            return resultImage;
        }

        public BufferedImage scaleImage(BufferedImage image, int width, int height) {
            Image imageScaled = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
            BufferedImage bufferedImageScaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D bGr = bufferedImageScaled.createGraphics();
            bGr.drawImage(imageScaled, 0, 0, null);
            bGr.dispose();

            return bufferedImageScaled;
        }

        public BufferedImage createMosaic(BufferedImage image, int finalImageWidth, int finalImageHeight) {

            int width = image.getWidth();
            int height = image.getHeight();
            int type = image.getType();
            int max = width / subImageSize;

            BufferedImage finalImage = new BufferedImage(finalImageWidth, finalImageHeight, type);

            try {
                for (int x = 0; x < width / subImageSize; x++) {
                    for (int y = 0; y < height / subImageSize; y++) {
                        int currentX = x * subImageSize;
                        int currentY = y * subImageSize;
                        BufferedImage subImage = image.getSubimage(currentX, currentY, subImageSize, subImageSize);
                        ArrayList<Integer> primaryColor = getPrimaryColor(subImage);
                        String fittingImageName = getImage(primaryColor.get(0), primaryColor.get(1), primaryColor.get(2));
                        File fittingImageFile = new File("results/" + fittingImageName);
                        BufferedImage fittingImage = ImageIO.read(fittingImageFile.toURI().toURL());
                        int fittingImageWidth = fittingImage.getWidth();
                        int fittingImageHeight = fittingImage.getHeight();
                        int[] rgbArray = new int[fittingImageWidth * fittingImageHeight];
                        fittingImage.getRGB(0, 0, fittingImageWidth, fittingImageHeight, rgbArray, 0, fittingImageWidth);
                        finalImage.setRGB(currentX * (dbImageSize / subImageSize), currentY * (dbImageSize / subImageSize), fittingImageWidth, fittingImageHeight, rgbArray, 0, fittingImageWidth);
                    }
                    
                    if (isCancelled()) {
                        break;
                     }
                    updateProgress(x, max);
                }
            }
            catch (IOException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }

            return finalImage;
        }

        public int getValidNumber(int number, int subimageSize) {
            if (number % subimageSize == 0) {
                return number;
            }

            int numberMinus = number;
            int numberPlus = number;

            while (true) {
                numberMinus--;
                numberPlus++;

                if (numberMinus % subimageSize == 0) {
                    return numberMinus;
                }
                if (numberPlus % subimageSize == 0) {
                    return numberPlus;
                }
            }
        }

        public boolean checkSubimageFits(int width, int height, int subimageSize) {
            return width % subimageSize == 0 && height % subimageSize == 0;
        }

        public ArrayList<Integer> getPrimaryColor(BufferedImage image) {
            int width = image.getWidth();
            int height = image.getHeight();

            int blue = 0;
            int green = 0;
            int red = 0;

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int color = image.getRGB(x, y);
                    blue += (color      ) & 0xFF;
                    green += (color >>  8) & 0xFF;
                    red += (color >> 16) & 0xFF;
                }
            }

            blue /= (width * height);
            green /= (width * height);
            red /= (width * height);

            ArrayList<Integer> colors = new ArrayList<>();
            colors.add(red);
            colors.add(green);
            colors.add(blue);

            return colors;
        }

        public String getImage(int red, int green, int blue) {
            Connection c = null;
            Statement stmt = null;
            ArrayList<String> names = new ArrayList<>();
            int plus = 0;

            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:" + this.database);

                while (names.isEmpty()) {

                    stmt = c.createStatement();
                    String sql = "SELECT name FROM IMAGES" +
                            " WHERE red BETWEEN " + outOfBounds(red - plus) + " and " + outOfBounds(red + plus) +
                            " AND green BETWEEN " + outOfBounds(green - plus) + " and " + outOfBounds(green + plus) +
                            " AND blue BETWEEN " + outOfBounds(blue - plus) + " and " + outOfBounds(blue + plus) + ";";
                    ResultSet results = stmt.executeQuery(sql);

                    while (results.next()) {
                        String name = results.getString("name");

                        names.add(name);
                    }

                    stmt.close();

                    plus += 5;
                }

                c.close();
            }
            catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }

            String result = null;
            int namesSize = names.size();
            if (namesSize > 1) {
                int rand = (int) Math.floor(Math.random() * namesSize);
                result = names.get(rand);
            }
            else {
                result = names.get(0);
            }

            return result;
        }

        public int outOfBounds(int number) {
            if (number >= 250) {
                return 255;
            }
            else if (number <= 5) {
                return 0;
            }
            else {
                return number;
            }
        }
    }

}