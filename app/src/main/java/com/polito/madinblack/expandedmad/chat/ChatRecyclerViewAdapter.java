package com.polito.madinblack.expandedmad.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.polito.madinblack.expandedmad.R;
import com.polito.madinblack.expandedmad.model.Message;
import com.polito.madinblack.expandedmad.model.MyApplication;

//questa classe la usa per fare il managing della lista che deve mostrare
public class ChatRecyclerViewAdapter extends FirebaseRecyclerAdapter<Message,RecyclerView.ViewHolder> {
    /**
     * @param modelClass      Firebase will marshall the data at a location into
     *                        an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list.
     *                        You will be responsible for populating an instance of the corresponding
     *                        view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location,
     *                        using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    private MyApplication ma;

    public ChatRecyclerViewAdapter(Class<Message> modelClass, int modelLayout, Class<RecyclerView.ViewHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        ma =  MyApplication.getInstance();
    }


    @Override
    protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Message model, int position) {
        //da cambiare il controllo, è solo per ricordarmi che se mio mess va a destra
        if(model.getSentById().equals(ma.getUserPhoneNumber())){
            ViewHolderRight holder = (ViewHolderRight)viewHolder;
            holder.mContentView.setText(model.getMessage());
        }
        else{
            ViewHolderLeft holder = (ViewHolderLeft)viewHolder;
            holder.mContentView.setText(model.getMessage());
            //devo poi settare la foto
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = null;
        if(viewType == 0){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_message_item_right, parent, false);
            return new ViewHolderRight(view);
        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_message_item_left, parent, false);
            return new ViewHolderLeft(view);
        }
    }

    @Override
    public int getItemViewType(int position){
        Message message = getItem(position);
        // 0 : right
        // 1 : left
        if(message.getSentById().equals(ma.getUserPhoneNumber()))
            return 0;
        else
            return 1;
    }

    //questa è una classe di supporto che viene usata per creare la vista a schermo
    public class ViewHolderRight extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public Message mItem;

        public ViewHolderRight(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.textView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    //questa è una classe di supporto che viene usata per creare la vista a schermo
    public class ViewHolderLeft extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mIdView;
        public final TextView mContentView;
        public Message mItem;

        public ViewHolderLeft(View view) {
            super(view);
            mView = view;
            mIdView = (ImageView) view.findViewById(R.id.imageUser);
            mContentView = (TextView) view.findViewById(R.id.textView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}