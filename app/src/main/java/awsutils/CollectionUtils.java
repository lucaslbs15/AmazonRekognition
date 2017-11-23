package awsutils;

import android.content.Context;
import android.util.Log;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.CreateCollectionRequest;
import com.amazonaws.services.rekognition.model.CreateCollectionResult;
import com.amazonaws.services.rekognition.model.DeleteCollectionRequest;
import com.amazonaws.services.rekognition.model.DeleteCollectionResult;
import com.amazonaws.services.rekognition.model.ListCollectionsRequest;
import com.amazonaws.services.rekognition.model.ListCollectionsResult;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtils {

    private static final String LOG_TAG = CollectionUtils.class.getSimpleName();

    public static void createCollection(Context context, String collectionId) {
        CreateCollectionResult result = callCreateCollection(collectionId, AmazonRekognitionUtils.getAmazonRekognition(context));
        Log.i(LOG_TAG, String.format("CreateCollection, CollectionId: %s, statusCode: %s: " + collectionId, result.getStatusCode()));
    }

    public static void deleteCollection(Context context, String collectionId) {
        DeleteCollectionResult result = callDeleteCollection(collectionId, AmazonRekognitionUtils.getAmazonRekognition(context));
        Log.i(LOG_TAG, String.format("DeleteCollection, CollectionId: %s, statusCode: %s: " + collectionId, result.getStatusCode()));
    }

    private static List<String> listCollections(Context context, int limit, String paginationToken) {
        List<String> resultList = new ArrayList<>();
        ListCollectionsResult result = null;
        do {
            if (result != null) {
                paginationToken = result.getNextToken();
            }
            result = callListCollections(paginationToken, limit, AmazonRekognitionUtils.getAmazonRekognition(context));
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(result.getCollectionIds())) {
                resultList.addAll(result.getCollectionIds());
            }
        } while (result.getNextToken() != null);
        return resultList;
    }

    private static CreateCollectionResult callCreateCollection(
            String collectionId,
            AmazonRekognition amazonRekognition) {
        CreateCollectionRequest request = new CreateCollectionRequest()
                .withCollectionId(collectionId);
        return amazonRekognition.createCollection(request);
    }

    private static DeleteCollectionResult callDeleteCollection(
            String collectionId,
            AmazonRekognition amazonRekognition) {
        DeleteCollectionRequest request = new DeleteCollectionRequest()
                .withCollectionId(collectionId);
        return amazonRekognition.deleteCollection(request);
    }

    private static ListCollectionsResult callListCollections(
            String paginationToken,
            int limit, AmazonRekognition amazonRekognition) {
        ListCollectionsRequest listCollectionsRequest = new ListCollectionsRequest()
                .withMaxResults(limit)
                .withNextToken(paginationToken);
        return amazonRekognition.listCollections(listCollectionsRequest);
    }
}
