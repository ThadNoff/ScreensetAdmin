package com.tnoffapps.screensetadmin;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.Console;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {

    Context context;
    String myuid;
    private DatabaseReference liekeref, postref, queueref, baseref, queueRef2, queueRef3;
    private FirebaseStorage storageCrap;
    boolean mprocesslike = false;
    private Context contexter;
    public AdapterPosts(Context context, List<ModelPost> modelPosts) {
        this.context = context;
        this.modelPosts = modelPosts;
        myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageCrap = FirebaseStorage.getInstance();
        baseref = FirebaseDatabase.getInstance().getReference();
        liekeref = FirebaseDatabase.getInstance().getReference().child("Likes");
        postref = FirebaseDatabase.getInstance().getReference().child("Posts");
        queueref = FirebaseDatabase.getInstance().getReference().child("PostQueue");
        queueRef2 = FirebaseDatabase.getInstance().getReference("PostQueue");
        queueRef3 = queueRef2.child("");
        Log.e("queueRef3", String.valueOf(queueRef3));
        Log.e("HEY", "HEY");
    }

    List<ModelPost> modelPosts;

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("Secondpostid", "postid2");

        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);
        return new MyHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        final String uid = modelPosts.get(position).getUid();
        String nameh = modelPosts.get(position).getUname();
        final String titlee = modelPosts.get(position).getTitle();
        final String descri = modelPosts.get(position).getDescription();
        final String ptime = modelPosts.get(position).getPtime();
        String dp = modelPosts.get(position).getUdp();
        String plike = modelPosts.get(position).getPlike();
        final String image = modelPosts.get(position).getUimage();
        final StorageReference photoRef;
        String email = modelPosts.get(position).getUemail();
        String comm = modelPosts.get(position).getPcomments();
        final String pid = modelPosts.get(position).getPtime();
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(ptime));
        String timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        holder.name.setText(nameh);
        holder.title.setText(titlee);
        holder.description.setText(descri);
        holder.time.setText(timedate);
        holder.like.setText(plike + " Likes");
        holder.comments.setText(comm + " Comments");
        setLikes(holder, ptime);
        try {
            Glide.with(context).load(dp).into(holder.picture);
        } catch (Exception e) {

        }
        holder.image.setVisibility(View.VISIBLE);
        try {
            Glide.with(context).load(image).into(holder.image);
        } catch (Exception e) {

        }
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), PostLikedByActivity.class);
                intent.putExtra("pid", pid);
                holder.itemView.getContext().startActivity(intent);
            }
        });
        holder.likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String postid2 = modelPosts.get(position).getPtime();
                Log.e("Secondpostid", postid2);
                Log.e("Queueref", String.valueOf(queueRef3));

                DatabaseReference combined = queueRef3.child(postid2);
                Log.e("Here is what combined is combining", postid2 + postref);
                DatabaseReference newToRef = postref.child(postid2);
                Log.e("What is combined?", String.valueOf(combined));
                Log.e("combined child", String.valueOf(combined.child("uimage")));
                DatabaseReference imageurl = (DatabaseReference) combined.child("uimage");
                deleteRecord(imageurl, combined);

                /*
                imageurl.addValueEventListener (new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot childSnapshot: dataSnapshot.getChildren()){
                            String imageRef = childSnapshot.getKey();
                            Log.e("Here is the image url ", imageRef);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Log.e("sdjkadasd", String.valueOf(imageurl));
                StorageReference tester = storageCrap.getReference(String.valueOf(imageurl));
                tester.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        Log.d("TAG", "onSuccess: deleted file");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        Log.d("TAG", "onFailure: did not delete file");
                    }
                });
                deleteRecord(combined, newToRef);*/
            }


        });
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions(holder.more, uid, myuid, ptime, image);
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String postid2 = modelPosts.get(position).getPtime();
                Log.e("Secondpostid", postid2);
                Log.e("Queueref", String.valueOf(queueRef3));

                DatabaseReference combined = queueRef3.child(postid2);
                DatabaseReference newToRef = postref.child(postid2);
                Log.e("What is combined?", String.valueOf(combined));
                moveRecord(combined, newToRef);
            }
        });

    }

    private void showMoreOptions(ImageButton more, String uid, String myuid, final String pid, final String image) {
        PopupMenu popupMenu = new PopupMenu(context, more, Gravity.END);
        if (uid.equals(myuid)) {
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "DELETE");
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == 0) {
                    deltewithImage(pid, image);
                }

                return false;
            }
        });
        popupMenu.show();
    }
    private void moveRecord(DatabaseReference fromPath, final DatabaseReference toPath) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("The data being moved i think",  dataSnapshot.getValue().toString());
                String keys = dataSnapshot.getKey();

                toPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            Log.d("TAG", "Success!");
                            fromPath.removeValue();
                        } else {
                            Log.d("TAG", "Copy failed!");
                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        fromPath.addListenerForSingleValueEvent(valueEventListener);
    }

    private void deleteRecord(DatabaseReference fromPath, DatabaseReference genPath) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("The data being moved i think",  dataSnapshot.getValue().toString());
                String keys = dataSnapshot.getKey();
                Log.e("What is the keys one?", keys);
                StorageReference testers = storageCrap.getReferenceFromUrl((String) dataSnapshot.getValue());

                genPath.getRef().removeValue();

                testers.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("The image located at this location has been deleted successfully" , (String) dataSnapshot.getValue());
                    }
                });



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        fromPath.addListenerForSingleValueEvent(valueEventListener);
    }
    private void deltewithImage(final String pid, String image) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting");
        StorageReference picref = FirebaseStorage.getInstance().getReferenceFromUrl(image);
        picref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {
                Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("ptime").equalTo(pid);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            dataSnapshot1.getRef().removeValue();
                        }
                        pd.dismiss();
                        Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_LONG).show();

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void setLikes(final MyHolder holder, final String pid) {
        liekeref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(pid).hasChild(myuid)) {
                    holder.likebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add, 0, 0, 0);
                    //holder.likebtn.setText("Liked");
                } else {
                    holder.likebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add, 0, 0, 0);
                    //holder.likebtn.setText("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return modelPosts.size();
    }
    public void YourNonActivityClass(Context context){
        this.context=context;
    }


    class MyHolder extends RecyclerView.ViewHolder {
        ImageView picture, image;
        TextView name, time, title, description, like, comments;
        ImageButton more;
        Button likebtn, comment;
        LinearLayout profile;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.picturetv);
            image = itemView.findViewById(R.id.pimagetv);
            name = itemView.findViewById(R.id.unametv);
            time = itemView.findViewById(R.id.utimetv);
            more = itemView.findViewById(R.id.morebtn);
            title = itemView.findViewById(R.id.ptitletv);
            description = itemView.findViewById(R.id.descript);
            like = itemView.findViewById(R.id.plikeb);
            comments = itemView.findViewById(R.id.pcommentco);
            likebtn = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            profile = itemView.findViewById(R.id.profilelayout);
        }
    }

}


