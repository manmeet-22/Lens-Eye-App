package com.manmeet.lenseye;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.manmeet.lenseye.adapters.ResultAdapter;
import com.manmeet.lenseye.database.HistoryContract;
import com.manmeet.lenseye.model.Result;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener {
    BottomSheetBehavior mBottomSheetBehaviour;
    @BindView(R.id.btnRetry)
    ImageButton retryButton;
    @BindView(R.id.cameraView)
    CameraView cameraView;
    @BindView(R.id.fab_take_photo)
    FloatingActionButton fabTakePhoto;
    @BindView(R.id.fabProgressCircle)
    FABProgressCircle fabProgressCircle;
    @BindView(R.id.framePreview)
    FrameLayout frameLayoutPreview;
    @BindView(R.id.imagePreview)
    ImageView imagePreview;
    @BindView(R.id.result_recycler_view)
    RecyclerView mResultRecyclerView;
    @BindView(R.id.bottomLayout)
    View mBottomSheet;
    private ArrayList<Result> mResultList;
    private ResultAdapter mResultAdapter;
    private SimpleGestureFilter detector;
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Stetho.initializeWithDefaults(this);

        cameraView.setLifecycleOwner(this);
        detector = new SimpleGestureFilter(this, this);
        mResultList = new ArrayList<Result>();
        mResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBottomSheetBehaviour = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Intent intent = getIntent();
        int flag = intent.getIntExtra("historyFlag",-1);
        if ( flag == 1){
            Gson gson = new Gson();
            String historyResultList = intent.getBundleExtra("historyBundle").getString("historyList");
            String historyImage = intent.getBundleExtra("historyBundle").getString("historyImage");
            final Type listType = new TypeToken<ArrayList<Result>>() {
            }.getType();
            final ArrayList<Result> resultList = gson.fromJson(historyResultList, listType);
            Type imageType = new TypeToken<byte[]>() {
            }.getType();
             byte[] bitmapArr = gson.fromJson(historyImage, imageType);
            final Bitmap result = BitmapFactory.decodeByteArray(bitmapArr, 0, bitmapArr.length);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            final Bitmap previewImage = Bitmap.createBitmap(result, 0, 0, result.getWidth()
                    , result.getHeight(), matrix, true);
            imagePreview.setImageBitmap(previewImage);
            mResultAdapter = new ResultAdapter(getBaseContext(), resultList);
            mResultRecyclerView.setAdapter(mResultAdapter);
            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);

        }
    }

    @OnClick(R.id.btnRetry)
    public void retryButtonClick() {
        if (cameraView.getVisibility() == View.VISIBLE) {
            showPreview();
        } else {
            hidePreview();
        }
    }

    @OnClick(R.id.fab_take_photo)
    public void fabTakeButton() {
        fabProgressCircle.show();
        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(final byte[] picture) {
                super.onPictureTaken(picture);
                CameraUtils.decodeBitmap(picture, new CameraUtils.BitmapCallback() {
                    @Override
                    public void onBitmapReady(final Bitmap bitmap) {
                        getLabelsFromDevice(bitmap, picture);
                        //getLabelsFromDevice(bitmap);
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        final Bitmap previewImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth()
                                , bitmap.getHeight(), matrix, true);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imagePreview.setImageBitmap(previewImage);
                                showPreview();
                            }
                        });
                    }
                });
            }
        });
        cameraView.capturePicture();
    }

    private void getLabelsFromDevice(Bitmap bitmap, final byte[] pictureByte) {
    //private void getLabelsFromDevice(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionLabelDetector detector = FirebaseVision.getInstance()
                .getVisionLabelDetector();
        Task<List<FirebaseVisionLabel>> result = detector.detectInImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<List<FirebaseVisionLabel>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionLabel> labels) {
                                mResultList.clear();
                                fabProgressCircle.hide();
                                for (FirebaseVisionLabel label : labels) {
                                    String text = label.getLabel();
                                    float confidence = label.getConfidence();
                                    Result res = new Result(text, confidence);
                                    mResultList.add(res);
                                }
                                mResultAdapter = new ResultAdapter(getBaseContext(), mResultList);
                                mResultRecyclerView.setAdapter(mResultAdapter);
                                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                                Gson gson = new Gson();
                                final String serializedHistoryList = gson.toJson(mResultList);
                                final String pictureByteString = gson.toJson(pictureByte);
                                AsyncTask<Void, Void, Void> insertHistoryTask = new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put(HistoryContract.HistoryEntry.COLUMN_IMAGE, pictureByteString);
                                        contentValues.put(HistoryContract.HistoryEntry.COLUMN_RESULT_LIST, serializedHistoryList);
                                        Uri rowsaffected = getContentResolver().insert(HistoryContract.HistoryEntry.CONTENT_URI, contentValues);
                                        Log.i(LOG_TAG, "doInBackground: "+rowsaffected );
                                        return null;
                                    }
                                };
                                insertHistoryTask.execute();
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                fabProgressCircle.hide();
                                Toast.makeText(getBaseContext(), "Sorry, something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }

    protected void showPreview() {
        frameLayoutPreview.setVisibility(View.VISIBLE);
        cameraView.setVisibility(View.GONE);
    }

    protected void hidePreview() {
        frameLayoutPreview.setVisibility(View.GONE);
        cameraView.setVisibility(View.VISIBLE);
    }

    protected void showHistoryPreview(){
        fabTakePhoto.setVisibility(View.GONE);
        fabProgressCircle.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);
        frameLayoutPreview.setVisibility(View.VISIBLE);
        cameraView.setVisibility(View.GONE);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {
        String str = "";

        switch (direction) {
            case SimpleGestureFilter.SWIPE_LEFT:
                str = "Swipe Left";
                Intent intent = new Intent(this, HistoryActivity.class);
                startActivity(intent);
                break;

/*
            case SimpleGestureFilter.SWIPE_RIGHT:
                str = "Swipe Right";
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                str = "Swipe Down";
                break;
            case SimpleGestureFilter.SWIPE_UP:
                str = "Swipe Up";
                break;
*/

        }
        //Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDoubleTap() {
        //Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }
}
