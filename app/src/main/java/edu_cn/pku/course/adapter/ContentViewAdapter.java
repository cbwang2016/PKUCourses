package edu_cn.pku.course.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import edu_cn.pku.course.Utils;
import edu_cn.pku.course.activities.ContentViewActivity;
import edu_cn.pku.course.activities.R;
import pub.devrel.easypermissions.EasyPermissions;

public class ContentViewAdapter extends RecyclerView.Adapter<ContentViewAdapter.ContentViewItemHolder> {

    private static final int WRITE_REQUEST_CODE = 300;
    private static String currentDownloadUrl = "";
    private static String currentDownloadFileName = "";

    private Context mContext;
    private ArrayList<ContentViewActivity.AttachedFileItem> list;

    public ContentViewAdapter(ArrayList<ContentViewActivity.AttachedFileItem> list, ContentViewActivity context) {
        this.list = list;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ContentViewItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ContentViewItemHolder(LayoutInflater.from(mContext).inflate(R.layout.item_view_content_attached_file, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ContentViewItemHolder holder, int i) {
        holder.content_view_file_name.setText(list.get(holder.getAdapterPosition()).getTitle());
        holder.content_view_file_size.setText(list.get(holder.getAdapterPosition()).getFileSize());

        holder.card_view_item_content.setClickable(true);
        holder.card_view_item_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isSDCardPresent()) {

                    currentDownloadUrl = list.get(holder.getAdapterPosition()).getUrl();
                    currentDownloadFileName = list.get(holder.getAdapterPosition()).getTitle();
                    //check if app has permission to write to the external storage.
                    if (EasyPermissions.hasPermissions(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //Get the URL entered
                        startDownload();

                    } else {
                        //If permission is not present request for the same.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            EasyPermissions.requestPermissions((Activity) mContext, "This app needs access to your file storage so that it can write files.", WRITE_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }
                } else {
                    Toast.makeText(mContext, "SD Card not found", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void startDownload() {
        new DownloadFile().execute();
    }

    public void updateList(ArrayList<ContentViewActivity.AttachedFileItem> item_list) {
        this.list = item_list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ContentViewItemHolder extends RecyclerView.ViewHolder {

        private CardView card_view_item_content;
        ImageView content_view_icon;
        private TextView content_view_file_name, content_view_file_size;

        ContentViewItemHolder(@NonNull View itemView) {
            super(itemView);
            content_view_file_name = itemView.findViewById(R.id.content_view_file_name);
            content_view_file_size = itemView.findViewById(R.id.content_view_file_size);
            card_view_item_content = itemView.findViewById(R.id.card_view_item_content);
            content_view_icon = itemView.findViewById(R.id.content_view_icon);
        }
    }

    /**
     * Async Task to download file from URL
     */
    @SuppressLint("StaticFieldLeak")
    private class DownloadFile extends AsyncTask<Void, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
//        private boolean isDownloaded;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(mContext);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(Void... params) {
            int count;
            try {
                SharedPreferences sharedPreferences = mContext.getSharedPreferences("login_info", Context.MODE_PRIVATE);
                String session_id = sharedPreferences.getString("session_id", null);

                URL url = new URL(currentDownloadUrl);
                URLConnection connection = url.openConnection();
                connection.setRequestProperty("Cookie", "session_id=" + session_id);
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                fileName = currentDownloadFileName;

                //External directory path to save file
                folder = Environment.getExternalStorageDirectory() + File.separator + "Download/PKU_Courses/";

                //Create androiddeft folder if it does not exist
                File directory = new File(folder);

                if (!directory.exists()) {
                    if (!directory.mkdirs()) {
                        throw new Exception("Error creating folder");
                    }
                }

                // Output stream to write file
                OutputStream output = new FileOutputStream(folder + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                return folder + fileName;

            } catch (Exception e) {
                return "Something went wrong" + e.getMessage();
            }
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();

            // Display File path after downloading
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();

            final Uri data = FileProvider.getUriForFile(mContext, "com.mypackage.myprovider", new File(message));
            mContext.grantUriPermission(mContext.getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            MimeTypeMap myMime = MimeTypeMap.getSingleton();
            Intent newIntent = new Intent(Intent.ACTION_VIEW);
            String mimeType = myMime.getMimeTypeFromExtension(fileExt(message).substring(1));
            newIntent.setDataAndType(data, mimeType);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                mContext.startActivity(newIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mContext, "No handler for this type of file.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @NonNull
    private String fileExt(String url) {
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return ".";
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.contains("%")) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.contains("/")) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();
        }
    }
}
