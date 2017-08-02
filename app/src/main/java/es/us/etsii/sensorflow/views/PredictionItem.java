package es.us.etsii.sensorflow.views;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import es.us.etsii.sensorflow.R;
import es.us.etsii.sensorflow.utils.Constants;

public class PredictionItem extends AbstractItem<PredictionItem, PredictionItem.ViewHolder> {

    // ------------------------- ATTRIBUTES --------------------------

    public long timestamp;
    public int activityRes;
    public int nameRes;
    public double totalTime;

    // ------------------------- CONSTRUCTOR -------------------------

    @Inject
    public PredictionItem() { }

    public PredictionItem withPrediction(int typeIndex, long timestamp) {
        this.timestamp = timestamp;
        this.activityRes = Constants.PREDICTION_IMAGES[typeIndex];
        this.nameRes = Constants.PREDICTION_NAMES[typeIndex];
        this.totalTime = Constants.M_ELAPSED_PER_SAMPLE;

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

    public long getTimestamp() {
        return timestamp;
    }

    // TODO Check this behaves as expected
    public void addToTotalTime(double valueToAdd) {
        this.totalTime += valueToAdd;
        Log.e("PredictionItem", "setTotalTime: " + totalTime + "Should update its value in the list");
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

        viewHolder.totalTimeTV.setText(String.format(Locale.getDefault(), "%f", totalTime));

        // TODO Consider using custom formatted String vs basic one (1 Context extra per item)
//        viewHolder.totalTimeTV.setText(Utils.getCustomDurationString(context, totalTime));
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
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView nameTV;
        protected TextView totalTimeTV;
        protected ImageView activityIV;

        public ViewHolder(View view) {
            super(view);

            this.nameTV = (TextView) view.findViewById(R.id.tv_item_activity);
            this.totalTimeTV = (TextView) view.findViewById(R.id.tv_item_time);
            this.activityIV = (ImageView) view.findViewById(R.id.iv_item_activity);
        }
    }
}