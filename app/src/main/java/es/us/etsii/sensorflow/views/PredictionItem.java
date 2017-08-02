package es.us.etsii.sensorflow.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.mikepenz.fastadapter.items.AbstractItem;
import java.util.List;
import javax.inject.Inject;
import es.us.etsii.sensorflow.R;
import es.us.etsii.sensorflow.utils.Constants;

public class PredictionItem extends AbstractItem<PredictionItem, PredictionItem.ViewHolder> {

    // ------------------------- ATTRIBUTES --------------------------

    private int predictionType;
    private int activityRes;
    private int nameRes;
    private CharSequence totalTimeCS;
    private double totalTimeD;

    // ------------------------- CONSTRUCTOR -------------------------

    @Inject
    public PredictionItem() { }

    PredictionItem withPrediction(int typeIndex, CharSequence totalTimeCS) {
        this.predictionType = typeIndex;
        this.activityRes = Constants.PREDICTION_IMAGES[typeIndex];
        this.nameRes = Constants.PREDICTION_NAMES[typeIndex];
        this.totalTimeD = Constants.S_ELAPSED_PER_SAMPLE;
        this.totalTimeCS = totalTimeCS;

        return this;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_item;
    }

    // -------------------------- AUXILIARY --------------------------

    @Override
    public int getType() {
        return R.id.rv_todays_activities;    // Unique ID per item
    }

    // -------------------------- USE CASES --------------------------

    double getTotalTime() {
        return totalTimeD;
    }

    int getPredictionType() {
        return predictionType;
    }

    void addToTotalTime(CharSequence totalTimeCS) {
        this.totalTimeD += Constants.S_ELAPSED_PER_SAMPLE;
        this.totalTimeCS = totalTimeCS;
    }

    /**
     * Logic to bind the activity data to the view
     */
    @Override
    public void bindView(ViewHolder viewHolder, List<Object> payloads) {
        super.bindView(viewHolder, payloads);

        // Binding data
        viewHolder.nameTV.setText(nameRes);
        viewHolder.activityIV.setImageResource(activityRes);
        viewHolder.totalTimeTV.setText(totalTimeCS);
    }

    //reset the view here (this is an optional method, but recommended)
    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
        holder.nameTV.setText(null);
        holder.totalTimeTV.setText(null);
//        holder.activityIV.setImageResource(-1);
    }

    //Init the viewHolder for this Item
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    // ------------------------- VIEW HOLDER -------------------------

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTV;
        TextView totalTimeTV;
        ImageView activityIV;

        ViewHolder(View view) {
            super(view);

            this.nameTV = (TextView) view.findViewById(R.id.tv_item_activity);
            this.totalTimeTV = (TextView) view.findViewById(R.id.tv_item_time);
            this.activityIV = (ImageView) view.findViewById(R.id.iv_item_activity);
        }
    }
}