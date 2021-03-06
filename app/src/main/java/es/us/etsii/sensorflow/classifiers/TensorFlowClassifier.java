package es.us.etsii.sensorflow.classifiers;

import android.content.Context;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import javax.inject.Inject;

import es.us.etsii.sensorflow.utils.Constants;

public class TensorFlowClassifier {

    private TensorFlowInferenceInterface inferenceInterface;

    private static final long[] INPUT_SIZE = {1, Constants.SAMPLE_SIZE, 3};
    private static final String INPUT_NODE = "inputs";
    private static final String[] OUTPUT_NODES = {"y_"};
    private static final String OUTPUT_NODE = "y_";

    @Inject
    TensorFlowClassifier(final Context context) {
        inferenceInterface = new TensorFlowInferenceInterface(context.getAssets(), Constants.MODEL_FILE);
    }

    public float[] predictProbabilities(float[] data) {
        float[] result = new float[Constants.OUTPUT_SIZE];
        inferenceInterface.feed(INPUT_NODE, data, INPUT_SIZE);
        inferenceInterface.run(OUTPUT_NODES);
        inferenceInterface.fetch(OUTPUT_NODE, result);
        return result;
    }
}
