package cn.ushang.myfire;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DownloadActivity extends AppCompatActivity {

    @InjectView(R.id.button2)
    Button upload;
    @InjectView(R.id.button3)
    Button download;
    @InjectView(R.id.imageView)
    ImageView imageView;

    private StorageReference mStorageRef;
    private Uri fileUri=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.inject(this);
        mStorageRef= FirebaseStorage.getInstance().getReference();

    }

    private void uploadFile(Uri uri){
        StorageReference imageRef=mStorageRef.child("photos")
                .child(uri.getLastPathSegment());
        imageRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri=taskSnapshot.getDownloadUrl();
                        Log.i("shao"," upload succes !"+downloadUri);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("shao"," upload fail : "+e);

                    }
                });
    }
    private void downloadFile(String path){
        StorageReference imageRef=mStorageRef.child(path);
        /*try {
            final File localFile = File.createTempFile("images", "jpg");
            File file=new File(Environment.getExternalStorageDirectory().getPath()+"/123.jpg");
            imageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created

                    Log.i("shao"," success "+localFile.getPath());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("shao"," failed "+exception);
                    // Handle any errors
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        /*final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.i("shao"," download success " +bytes.length);
                        Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        imageView.setImageBitmap(bitmap);
                        Log.i("shao"," download success ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("shao"," download fail : "+e);
                    }
                });*/
        mStorageRef.child(path).getStream(
                new StreamDownloadTask.StreamProcessor() {
                    @Override
                    public void doInBackground(StreamDownloadTask.TaskSnapshot taskSnapshot, InputStream inputStream) throws IOException {
                        Bitmap bitmap=BitmapFactory.decodeStream(inputStream);
                        imageView.setImageBitmap(bitmap);
                    }
                }
        ).addOnSuccessListener(new OnSuccessListener<StreamDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(StreamDownloadTask.TaskSnapshot taskSnapshot) {
                Log.i("shao"," download success ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("shao"," download failed ");
            }
        });


    }
    private void selectImage(){
        Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                fileUri=data.getData();
                if(fileUri!=null){
                    uploadFile(fileUri);
                }
            }
        }
    }

    @OnClick({R.id.button2, R.id.button3})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button2:
            selectImage();
                break;
            case R.id.button3:
                downloadFile("photos/IMG_20151129_174835.jpg");
                break;
        }
    }
}
