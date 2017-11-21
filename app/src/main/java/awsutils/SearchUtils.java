package awsutils;

import android.util.Log;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.IndexFacesRequest;
import com.amazonaws.services.rekognition.model.IndexFacesResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.SearchFacesByImageRequest;
import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;
import com.amazonaws.services.rekognition.model.SearchFacesRequest;
import com.amazonaws.services.rekognition.model.SearchFacesResult;

public class SearchUtils {

    private static String LOG_TAG = SearchUtils.class.getSimpleName();

    public static void searchFaces(String collectionId, String imageName, String bucket, Float threshold, int maxFaces) {
        IndexFacesResult indexFacesResult = callIndexFaces(collectionId, AmazonRekognitionUtils.getAmazonRekognition(), imageName, bucket);
        String faceId = null;
        if (com.amazonaws.util.CollectionUtils.isNullOrEmpty(indexFacesResult.getFaceRecords())) {
            faceId = indexFacesResult.getFaceRecords().get(0).getFace().getFaceId();
        }
        if (faceId == null) return;

        SearchFacesResult result = callSearchFaces(collectionId, faceId, threshold, maxFaces, AmazonRekognitionUtils.getAmazonRekognition());
        for (FaceMatch faceMatch : result.getFaceMatches()) {
            //TODO retornar esse resultado
            Log.i(LOG_TAG, String.format("Face Match: %s", faceMatch.getFace().toString()));
        }
    }

    private static IndexFacesResult callIndexFaces(
            String collectionId,
            AmazonRekognition amazonRekognition,
            String name, String bucket) {
        IndexFacesRequest req = new IndexFacesRequest()
                .withImage(ImageUtils.getImageUtil(bucket, name))
                .withCollectionId(collectionId)
                .withExternalImageId(name);
        return amazonRekognition.indexFaces(req);
    }

    private static SearchFacesByImageResult callSearchFacesByImage(
            String collectionId, Image image, Float threshold,
            int maxFaces, AmazonRekognition amazonRekognition) {
        SearchFacesByImageRequest searchFacesByImageRequest = new SearchFacesByImageRequest()
                .withCollectionId(collectionId)
                .withImage(image)
                .withFaceMatchThreshold(threshold)
                .withMaxFaces(maxFaces);
        return amazonRekognition.searchFacesByImage(searchFacesByImageRequest);
    }

    private static SearchFacesResult callSearchFaces(
            String collectionId, String faceId, Float threshold,
            int maxFaces, AmazonRekognition amazonRekognition) {
        SearchFacesRequest searchFacesRequest = new SearchFacesRequest()
                .withCollectionId(collectionId)
                .withFaceId(faceId)
                .withFaceMatchThreshold(threshold)
                .withMaxFaces(maxFaces);
        return amazonRekognition.searchFaces(searchFacesRequest);
    }
}
