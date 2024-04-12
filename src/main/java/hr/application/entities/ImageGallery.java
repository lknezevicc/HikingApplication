package hr.application.entities;

import hr.application.exceptions.ImageLoadingException;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

public final class ImageGallery implements ImageGalleryAction {
    private List<ImageRepresentation> images;
    private int currentImageIndex;

    public ImageGallery() {
        images = new ArrayList<>();
        this.currentImageIndex = 0;
    }

    @Override
    public ImageGallery nextImage() {
        if (currentImageIndex < images.size() - 1) {
            currentImageIndex++;
        }

        return this;
    }

    @Override
    public ImageGallery previousImage() {
        if (currentImageIndex > 0) {
            currentImageIndex--;
        }

        return this;
    }

    @Override
    public ImageRepresentation getCurrentImage() {
        return images.get(currentImageIndex);
    }

    @Override
    public void updateImageView(ImageView imageView, Label description) throws ImageLoadingException {
        ImageRepresentation currentImage = getCurrentImage();
        try {
            imageView.setImage(new Image(currentImage.getImageFile().toURI().toString()));
        } catch (IllegalArgumentException e) {
            throw new ImageLoadingException("Error loading image from " + currentImage.getImageFile().toURI());
        }
        description.setText(currentImage.getDescription());
    }

    public List<ImageRepresentation> getImages() {
        return images;
    }

    public void setImages(List<ImageRepresentation> images) {
        this.images = images;
    }

    public int getCurrentImageIndex() {
        return currentImageIndex;
    }

    public void setCurrentImageIndex(int currentImageIndex) {
        this.currentImageIndex = currentImageIndex;
    }
}
