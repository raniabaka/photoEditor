package rania.myproject.com.photoproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Range;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    final static int CAMERA_RESULT = 0;
    public Mat imgToProcess, mRgba, kernMat;
    String mCurrentPhotoPath;
    ImageView imv;
    Bitmap bmp, bmpOut, bmpOut2;
    RelativeLayout brRL, saveRL, thRL, blurRL;
    ImageButton brIButton, grayIButton, lapButton, flipButton, thButton,
            blurButton, saveButton, cannyButton, eyeButton, bronzeButton,
            cyanButton, exitButton;
    Button savButton;
    SeekBar brSeekBar, thSeekBar, blSeekBar;
    public String filename;
    EditText saveText;
    public int red, green, blue;
    public Scalar m;


    private static final String TAG = "MainActivity";

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imv = (ImageView) findViewById(R.id.imageViewPhoto);
        brRL = (RelativeLayout) findViewById(R.id.brRL);
        thRL = (RelativeLayout) findViewById(R.id.ThresholdRL);
        brIButton = (ImageButton) findViewById(R.id.brIButton);
        brSeekBar = (SeekBar) findViewById(R.id.brSeekBar);
        grayIButton = (ImageButton) findViewById(R.id.grayIButton);
        flipButton = (ImageButton) findViewById(R.id.flipButton);
        lapButton = (ImageButton) findViewById(R.id.laplaceButton);
        thButton = (ImageButton) findViewById(R.id.thresholdButton);
        thSeekBar = (SeekBar) findViewById(R.id.thresholdseekBar);
        blurRL = (RelativeLayout) findViewById(R.id.BlurRL);
        blurButton = (ImageButton) findViewById(R.id.blurButton);
        blSeekBar = (SeekBar) findViewById(R.id.blurSeekBar);
        cannyButton = (ImageButton) findViewById(R.id.cannyButton);
        eyeButton = (ImageButton) findViewById(R.id.eyeButton);
        bronzeButton = (ImageButton) findViewById(R.id.bronzeButton);
        cyanButton = (ImageButton) findViewById(R.id.cyanButton);
        saveRL = (RelativeLayout) findViewById(R.id.saveRL);
        saveButton = (ImageButton) findViewById(R.id.saveButton);
        savButton = (Button) findViewById(R.id.savbutton);
        saveText = (EditText) findViewById(R.id.saveText);
        exitButton = (ImageButton) findViewById(R.id.exitButton);

        VisiBility(1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_RESULT && resultCode == RESULT_OK) {

            int targetW = imv.getWidth();
            int targetH = imv.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            bmp = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            bmp = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            imv.setImageBitmap(bmp);

            Mat src = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC4);
            imgToProcess = Utils.bitmapToMat(bmp, src);
            kernMat = new Mat();
            mRgba = new Mat(imgToProcess, Range.all());
            //mRgba=new Mat(mRgba.rows(), mRgba.cols(), imgToProcess.type());
            bmpOut = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(imgToProcess, bmpOut);
            imv.setImageBitmap(bmpOut);
            rotateImage(bmpOut);

            brIButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    VisiBility(2);
                }
            });
            flipButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filter(1);
                    rotateImage(filter(1));
                }
            });
            grayIButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imv.setImageBitmap(filter(2));
                    rotateImage(filter(2));
                }
            });
            lapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imv.setImageBitmap(filter(3));
                    rotateImage(filter(3));
                }
            });
            thButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imv.setImageBitmap(filter(4));
                    rotateImage(filter(4));
                }
            });
            blurButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imv.setImageBitmap(filter(5));
                    rotateImage(filter(5));
                }
            });
            cannyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imv.setImageBitmap(filter(6));
                    rotateImage(filter(6));
                }
            });
            eyeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imv.setImageBitmap(filter(7));
                    rotateImage(filter(7));
                }
            });
            bronzeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filter(8);
                    rotateImage(filter(8));
                }
            });
            cyanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    filter(9);
                    rotateImage(filter(9));
                }
            });
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VisiBility(3);
                }
            });
            savButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String filename = saveText.getText().toString();
                    try {
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        OutputStream fOut = null;
                        File file = new File(path, filename + ".jpg");
                        fOut = new FileOutputStream(file);
                        bmpOut2.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                        fOut.flush();
                        fOut.close();
                        MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(),
                                file.getName(), file.getName());
                    } catch (Exception e) {
                    }
                }
            });
            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    System.exit(0);
                }
            });

            brSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    bmpOut2 = increaseBrightness(bmp, progress);
                    imv.setImageBitmap(bmpOut2);
                    rotateImage(bmpOut2);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            thSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Imgproc.cvtColor(imgToProcess, mRgba, Imgproc.COLOR_BGR2GRAY);
                    Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
                    Imgproc.threshold(mRgba, mRgba, progress, 0, Imgproc.THRESH_TOZERO);
                    bmpOut = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(mRgba, bmpOut);
                    imv.setImageBitmap(bmpOut);
                    rotateImage(bmpOut);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            blSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (progress % 2 == 0) {
                        progress = progress + 1;
                    }
                    Imgproc.GaussianBlur(imgToProcess, mRgba, new Size(progress, progress), 40);
                    bmpOut = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(mRgba, bmpOut);
                    imv.setImageBitmap(bmpOut);
                    rotateImage(bmpOut);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }

    public void takePhoto(View view) {
        Intent callCamera = new Intent();
        callCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        callCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        startActivityForResult(callCamera, CAMERA_RESULT);

    }

    File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void rotateImage(Bitmap bitmap) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(mCurrentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            default:
        }
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        imv.setImageBitmap(rotatedBitmap);

    }

    private Bitmap increaseBrightness(Bitmap bitmap, int value) {

        Mat src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, src);
        src.convertTo(src, -1, 1, value);
        Bitmap result = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src, result);
        return result;
    }

    public void VisiBility(int visible) {
        if (visible == 1) {
            brRL.setVisibility(View.GONE);
            saveRL.setVisibility(View.GONE);
            thRL.setVisibility(View.GONE);
            blurRL.setVisibility(View.GONE);
        } else if (visible == 2) {
            brRL.setVisibility(View.VISIBLE);
            saveRL.setVisibility(View.GONE);
            thRL.setVisibility(View.GONE);
            blurRL.setVisibility(View.GONE);
        } else if (visible == 3) {
            brRL.setVisibility(View.GONE);
            saveRL.setVisibility(View.VISIBLE);
            thRL.setVisibility(View.GONE);
            blurRL.setVisibility(View.GONE);
        } else if (visible == 4) {
            brRL.setVisibility(View.GONE);
            saveRL.setVisibility(View.GONE);
            thRL.setVisibility(View.VISIBLE);
            blurRL.setVisibility(View.GONE);
        } else if (visible == 5) {
            brRL.setVisibility(View.GONE);
            saveRL.setVisibility(View.GONE);
            thRL.setVisibility(View.GONE);
            blurRL.setVisibility(View.VISIBLE);
        }
    }

    protected Bitmap filter(int i) {
        Mat src = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC4);
        imgToProcess = Utils.bitmapToMat(bmp, src);
        switch (i) {
            case 1:
                filename = "Flip";
                VisiBility(1);
                Core.flip(imgToProcess, mRgba, 0);
                break;
            case 2:
                filename = "Grayscale";
                VisiBility(1);
                Imgproc.cvtColor(imgToProcess, mRgba, Imgproc.COLOR_BGR2GRAY);
                Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
                break;
            case 3:
                filename = "Laplace";
                VisiBility(1);
                Imgproc.cvtColor(imgToProcess, mRgba, Imgproc.COLOR_BGRA2GRAY);
                Imgproc.Laplacian(mRgba, mRgba, mRgba.depth());
                Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
                break;
            case 4:
                filename = "threshold";
                VisiBility(4);
                Imgproc.cvtColor(imgToProcess, mRgba, Imgproc.COLOR_BGR2GRAY);
                Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
                Imgproc.threshold(mRgba, mRgba, 0, 0, Imgproc.THRESH_TOZERO);
                break;
            case 5:
                filename = "GaussianBlur";
                VisiBility(5);
                Imgproc.GaussianBlur(imgToProcess, mRgba, new Size(1, 1), 40);
                break;
            case 6:
                filename = "Canny";
                VisiBility(1);
                Imgproc.cvtColor(imgToProcess, mRgba, Imgproc.COLOR_BGRA2GRAY, 4);
                Imgproc.Canny(mRgba, mRgba, 590, 600, 3);
                Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
                break;
            case 7:
                filename="eyefilter";
                VisiBility(1);
                kernMat = Mat.eye(new Size(4, 4), CvType.CV_64FC1);
                Imgproc.filter2D(imgToProcess, mRgba, 64, kernMat);
                break;
            case 8:
                filename="Bronze";
                VisiBility(1);
                RGB(205, 127, 50);
                mRgba = RGBconv(imgToProcess, mRgba, kernMat);
                break;
            case 9:
                filename="Cyan";
                VisiBility(1);
                RGB(0, 100, 200);
                mRgba = RGBconv(imgToProcess, mRgba, kernMat);
                break;
            default:
                break;
        }
        bmpOut2 = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mRgba, bmpOut2);
        return bmpOut2;
    }
    public void RGB(int r, int g, int b) {
        red = r;
        green = g;
        blue = b;
    }
    public Mat RGBconv(Mat src, Mat dst, Mat kernel) {
        m = new Scalar(this.red, this.green, this.blue);
        kernel = new Mat(src, Range.all());
        kernel = new Mat(kernel.size(), src.type(), m);
        Core.add(src, kernel, dst);
        return dst;
    }
}