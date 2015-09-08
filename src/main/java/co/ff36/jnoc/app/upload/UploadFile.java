/**
 *  Copyright (C) 2015  555 inc ltd.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/

package co.ff36.jnoc.app.upload;

import co.ff36.jnoc.per.project.JNOC.UploadType;

/**
 * When a file is uploaded all the respective details about that file are
 * transfered into this object. The file itself is not present! If the file is
 * stored either locally or externally the URI to the file is stored.
 *
 * @version 3.0.0
 * @since Build 3.0.0 (May 9, 2014)
 * @author Tarka L'Herpiniere

 */
public class UploadFile {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private FileMeta meta;
    private Image image;
    private UploadType type;
    private boolean uploaded;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public UploadFile() {
        this.meta = new FileMeta();
        this.image = new Image();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Get the value of meta
     *
     * @return the value of meta
     */
    public FileMeta getMeta() {
        return meta;
    }

    /**
     * Get the value of image
     *
     * @return the value of image
     */
    public Image getImage() {
        return image;
    }

    /**
     * Get the value of type. The type of uploaded file
     *
     * @return the value of type.
     */
    public UploadType getType() {
        return type;
    }

    /**
     * Get the value of uploaded. Flag to indicate whether the file was
     * successfully uploaded.
     *
     * @return the value of uploaded.
     */
    public boolean isUploaded() {
        return uploaded;
    }

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Set the value of meta
     *
     * @param meta new value of meta
     */
    public void setMeta(FileMeta meta) {
        this.meta = meta;
    }

    /**
     * Set the value of image
     *
     * @param image new value of image
     */
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * Set the value of type. The uploaded file type.
     *
     * @param type new value of type
     */
    public void setType(UploadType type) {
        this.type = type;
    }

    /**
     * Set the value of uploaded. Flag to indicate whether the file was
     * successfully uploaded.
     *
     * @param uploaded new value of uploaded
     */
    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

//</editor-fold>
    
    /**
     * Holds Meta data relating to the uploaded file
     *
     * @version 3.0.0
     * @since Build 3.0.0 (May 9, 2014)
     * @author Tarka L'Herpiniere
    
     */
    public class FileMeta {

        //<editor-fold defaultstate="collapsed" desc="Properties">
        private String uri;
        private String s3Key;
        private String newName;
        private String originalName;
        private String contentType;
        private long size;
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Constructors">
        public FileMeta() {
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Getters">
        /**
         * Get the value of uri
         *
         * @return the value of uri
         */
        public String getUri() {
            return uri;
        }

        /**
         * Get the value of s3Key
         *
         * @return the value of s3Key
         */
        public String getS3Key() {
            return s3Key;
        }

        /**
         * Get the value of newName
         *
         * @return the value of newName
         */
        public String getNewName() {
            return newName;
        }

        /**
         * Get the value of originalName
         *
         * @return the value of originalName
         */
        public String getOriginalName() {
            return originalName;
        }

        /**
         * Get the value of contentType (MIME type)
         *
         * @return the value of contentType
         */
        public String getContentType() {
            return contentType;
        }

        /**
         * Get the value of size in bytes
         *
         * @return the value of size
         */
        public long getSize() {
            return size;
        }

//</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Setters">
        /**
         * Set the value of uri
         *
         * @param uri new value of uri
         */
        public void setUri(String uri) {
            this.uri = uri;
        }

        /**
         * Set the value of s3Key
         *
         * @param s3Key new value of s3Key
         */
        public void setS3Key(String s3Key) {
            this.s3Key = s3Key;
        }

        /**
         * Set the value of newName
         *
         * @param newName new value of newName
         */
        public void setNewName(String newName) {
            this.newName = newName;
        }

        /**
         * Set the value of originalName
         *
         * @param originalName new value of originalName
         */
        public void setOriginalName(String originalName) {
            this.originalName = originalName;
        }

        /**
         * Set the value of contentType
         *
         * @param contentType new value of contentType
         */
        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        /**
         * Set the value of byte size
         *
         * @param size new value of size
         */
        public void setSize(long size) {
            this.size = size;
        }
        //</editor-fold>

    }

    /**
     * If the upload is an image the image details are stored here.
     *
     * @version 3.0.0
     * @since Build 3.0.0 (May 9, 2014)
     * @author Tarka L'Herpiniere
    
     */
    public class Image {

        //<editor-fold defaultstate="collapsed" desc="Properties">
        private int width;
        private int height;
        private ImageCrop imageCrop;
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Constructors">
        public Image() {
            this.imageCrop = new ImageCrop();
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Getters">
        /**
         * Get the value of height
         *
         * @return the value of height
         */
        public int getHeight() {
            return height;
        }

        /**
         * Get the value of width
         *
         * @return the value of width
         */
        public int getWidth() {
            return width;
        }

        /**
         * Get the value of imageCrop
         *
         * @return the value of imageCrop
         */
        public ImageCrop getImageCrop() {
            return imageCrop;
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Setters">
        /**
         * Set the value of width
         *
         * @param width new value of width
         */
        public void setWidth(int width) {
            this.width = width;
        }

        /**
         * Set the value of height
         *
         * @param height new value of height
         */
        public void setHeight(int height) {
            this.height = height;
        }

        /**
         * Set the value of imageCrop
         *
         * @param imageCrop new value of imageCrop
         */
        public void setImageCrop(ImageCrop imageCrop) {
            this.imageCrop = imageCrop;
        }
//</editor-fold>

    }

    /**
     * If the upload is an image the possibility exists that the original image
     * can be cropped. This holds crop dimension.
     *
     * @version 3.0.0
     * @since Build 3.0.0 (May 9, 2014)
     * @author Tarka L'Herpiniere
    
     */
    public class ImageCrop {

        //<editor-fold defaultstate="collapsed" desc="Properties">
        private int x;
        private int y;
        private int width;
        private int height;
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Constructors">
        public ImageCrop() {
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Getters">
        /**
         * Get the value of height
         *
         * @return the value of height
         */
        public int getHeight() {
            return height;
        }

        /**
         * Get the value of width
         *
         * @return the value of width
         */
        public int getWidth() {
            return width;
        }

        /**
         * Get the value of y
         *
         * @return the value of y
         */
        public int getY() {
            return y;
        }

        /**
         * Get the value of x
         *
         * @return the value of x
         */
        public int getX() {
            return x;
        }
//</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Setters">
        /**
         * Set the value of x
         *
         * @param x new value of x
         */
        public void setX(int x) {
            this.x = x;
        }

        /**
         * Set the value of y
         *
         * @param y new value of y
         */
        public void setY(int y) {
            this.y = y;
        }

        /**
         * Set the value of width
         *
         * @param width new value of width
         */
        public void setWidth(int width) {
            this.width = width;
        }

        /**
         * Set the value of height
         *
         * @param height new value of height
         */
        public void setHeight(int height) {
            this.height = height;
        }
//</editor-fold>

    }

}
