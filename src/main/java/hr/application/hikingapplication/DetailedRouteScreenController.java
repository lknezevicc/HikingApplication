package hr.application.hikingapplication;

import hr.application.entities.HikingRoute;
import hr.application.entities.ImageGallery;
import hr.application.exceptions.ImageLoadingException;
import hr.application.scenemanagement.SceneManager;
import hr.application.utilities.DatabaseUtility;
import hr.application.utilities.TimeConvert;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetailedRouteScreenController {
    private static final Logger logger = LoggerFactory.getLogger(DetailedRouteScreenController.class);
    private final ImageGallery imageGallery = new ImageGallery();
    private HikingRoute route;
    @FXML
    private Label routeName;
    @FXML
    private Label imageGalleryState;
    @FXML
    private ImageView image;
    @FXML
    private Button leftButton;
    @FXML
    private Button rightButton;
    @FXML
    private Label imageDescription;
    @FXML
    private Label mountainPeak;
    @FXML
    private Label startingPoint;
    @FXML
    private Label endingPoint;
    @FXML
    private Label length;
    @FXML
    private Label duration;
    @FXML
    private Label difficulty;
    @FXML
    private Label author;
    @FXML
    private TextArea description;

    public void initialize() {
        route = SceneManager.getInstance().getSharedObject(HikingRoute.class);
        SceneManager.getInstance().clearSharedObject();

        configureUI();

        imageGallery.getImages().addAll(DatabaseUtility.getImagesForRoute(route.getID()));
        displayGallery();
    }

    private void displayGallery() {
        boolean hasImages = !imageGallery.getImages().isEmpty();
        int currentIndex = imageGallery.getCurrentImageIndex();
        int lastIndex = imageGallery.getImages().size() - 1;

        imageGalleryState.setVisible(!hasImages);
        image.setVisible(hasImages);
        leftButton.setVisible(hasImages && currentIndex > 0);
        rightButton.setVisible(hasImages && currentIndex < lastIndex);
        imageDescription.setVisible(hasImages);
        if (hasImages) {
            try {
                imageGallery.updateImageView(image, imageDescription);
            } catch (ImageLoadingException e) {
                logger.error("Error while loading image!, [{}]", e.getMessage());
            }
        }
    }

    public void leftButtonAction() {
        try {
            imageGallery.previousImage().updateImageView(image, imageDescription);
        } catch (ImageLoadingException e) {
            logger.error("Error while loading image!, [{}]", e.getMessage());
        }
        displayGallery();
    }

    public void rightButtonAction() {
        try {
            imageGallery.nextImage().updateImageView(image, imageDescription);
        } catch (ImageLoadingException e) {
            logger.error("Error while loading image!, [{}]", e.getMessage());
        }
        displayGallery();
    }

    private void configureUI() {
        routeName.setText(route.getName());
        mountainPeak.setText(route.getMountainPeak());
        startingPoint.setText(route.getStartingPoint());
        endingPoint.setText(route.getEndingPoint());
        length.setText(String.format("%.2f km", route.getLength()));
        duration.setText(TimeConvert.convertToHoursAndMinutesString(route.getDuration()) + " h");
        difficulty.setText(String.format("%d / 10", route.getDifficulty()));
        author.setText(route.getAuthor());
        description.setText(route.getDescription());
    }

}
