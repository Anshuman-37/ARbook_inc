package com.singh.integrated_book;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ArCore_Img extends AppCompatActivity{
    private static final String TAG = ArCore_Img.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;
    private ArFragment arFragment;
    private ArSceneView arSceneView;
    private ViewRenderable Renderable;
    private boolean hasFinishedLoading = false;
    static int ctr = 0;

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    // CompletableFuture requires api level 24
    // FutureReturnValueIgnored is not valid
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.activity_ux);
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
                    anchorNode.setRenderable(Renderable);
                    anchorNode.setLocalPosition(new Vector3(0.0f, 0.15f, 0.0f));
                    //ctr++;
                });
    }


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



