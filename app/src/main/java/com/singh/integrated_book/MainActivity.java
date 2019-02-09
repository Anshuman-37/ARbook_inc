package com.singh.integrated_book;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.filament.View;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.singh.integrated_book.ArCore_Img.checkIsSupportedDeviceOrFinish;

public class MainActivity extends AppCompatActivity {
private boolean yes=true;
    private static final String TAG = ArCore_Img.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    private ArFragment arFragment;
    private ArSceneView arSceneView;
    private ViewRenderable Renderable, imageV;
    private boolean hasFinishedLoading = false;
        ImageView img;
        ImageView img2;
        Button b;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            img = (ImageView)findViewById(R.id.Image);
            img2=(ImageView) findViewById(R.id.Imagetest);
            Intent intent2 = new Intent(this,ArCore_Img.class);
            b=findViewById(R.id.BTn);


            ViewRenderable.builder().setView(getApplicationContext(), R.layout.images).build()
                    .thenAccept(renderable -> imageV = renderable);




        b.setOnClickListener(view -> {
            /*Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(intent,100);
            img2=img;
            yes=true;*/
            ImagePicker.create(this)
                    .start();

        });
          /*  if(yes==true) {
                startActivity(intent2);
            }*/
            if (!checkIsSupportedDeviceOrFinish(this)) {
                return;
            }

            arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);


            CompletableFuture<ViewRenderable> renederable = ViewRenderable.builder()
                    .setView(arFragment.getContext(),R.layout.textviewtest)
                    .build();
            CompletableFuture.allOf(
                    renederable
            )
                    .handle(
                            (notUsed, throwable) -> {
                                // When you build a Renderable, Sceneform loads its resources in the background while
                                // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
                                // before calling get().
                                if (throwable != null) {
                                    //maine dalla hai
                                    Toast.makeText(this, "game Over", Toast.LENGTH_SHORT).show();
                                    return null;
                                }

                                try {
                                    Renderable = renederable.get();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }

                                // Everything finished loading successfully.
                                hasFinishedLoading = true;

                                return null;
                            });


      /*      arFragment.setOnTapArPlaneListener(
                    (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                        // Create the Anchor.
                        Anchor anchor = hitResult.createAnchor();
                        if (anchor != null) {
                            anchor.detach();
                        }

                        Toast.makeText(this, "YAHAN Paunchgya Bhaiya", Toast.LENGTH_SHORT).show();
                        // Anchor anchor = hitResult.createAnchor();
                        AnchorNode anchorNode = new AnchorNode(anchor);
                        anchorNode.setParent(arFragment.getArSceneView().getScene());
                        anchorNode.setRenderable(Renderable);
                        anchorNode.setLocalPosition(new Vector3(0.0f, 0.15f, 0.0f));
                        //ctr++;
                    });*/

        }




    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            List<Image> images = ImagePicker.getImages(data);
            // or get a singl+e image only

            Image image = ImagePicker.getFirstImageOrNull(data);

            File file = new File(images.get(0).getPath());

            Uri uri = Uri.fromFile(new File(file.getPath()));


            arFragment.setOnTapArPlaneListener(
                    (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                        // Create the Anchor.
                        Anchor anchor = hitResult.createAnchor();
                        if (anchor != null) {
                            anchor.detach();
                        }

                        Toast.makeText(this, "YAHAN Paunchgya Bhaiya", Toast.LENGTH_SHORT).show();
                        // Anchor anchor = hitResult.createAnchor();
                        AnchorNode anchorNode = new AnchorNode(anchor);
                        anchorNode.setParent(arFragment.getArSceneView().getScene());
                        anchorNode.setRenderable(imageV);
                        android.view.View v = imageV.getView();
                        ImageView img = v.findViewById(R.id.img);

                        Picasso.get().load(uri).into(img);
                        anchorNode.setLocalPosition(new Vector3(0.0f, 0.15f, 0.0f));
                        //ctr++;
                    });

            Toast.makeText(getApplicationContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
   /*
    @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode==100 && resultCode==RESULT_OK)
            {
                Uri uri = data.getData();
                img.setImageURI(uri);
            }
        }*/

    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

}

