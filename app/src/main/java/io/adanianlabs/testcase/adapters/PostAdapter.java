package io.adanianlabs.testcase.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import io.adanianlabs.testcase.R;
import io.adanianlabs.testcase.models.Post;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Post> items = new ArrayList<>();

    private View root;

    private Context context;
    private PostAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(PostAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public PostAdapter(Context context, List<Post> items) {
        this.items = items;
        this.context = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvTime;
        public TextView tvConnectiions;
        public TextView tvPostText;
        public ImageView imgPost;
        public ImageView imgProfilePic;




        public OriginalViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.tv_name);
            tvTime = (TextView) v.findViewById(R.id.tv_time);
            tvConnectiions = (TextView) v.findViewById(R.id.tv_connections);
            tvPostText = (TextView) v.findViewById(R.id.tv_post_text);
            imgPost = (ImageView) v.findViewById(R.id.img_post);
            imgProfilePic = (ImageView) v.findViewById(R.id.img_profile_pic);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        vh = new PostAdapter.OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Post obj = items.get(position);
        if (holder instanceof PostAdapter.OriginalViewHolder) {
            PostAdapter.OriginalViewHolder view = (PostAdapter.OriginalViewHolder) holder;

            view.tvName.setText(obj.getUsername());
            view.tvConnectiions.setText(obj.getType()+" Types");
            view.tvPostText.setText(obj.getPost());

            view.imgPost.setImageURI(Uri.parse(obj.getMediaurl()));
            view.imgProfilePic.setImageURI(Uri.parse(obj.getProfilepicture()));


        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
