package hr.application.entities;

import hr.application.exceptions.ImageLoadingException;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public sealed interface ImageGalleryAction permits ImageGallery {
    ImageGallery nextImage();
    ImageGallery previousImage();
    ImageRepresentation getCurrentImage();
    void updateImageView(ImageView imageView, Label description) throws ImageLoadingException;

}
