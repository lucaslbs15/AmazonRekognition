package awsutils;

import android.util.Log;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.Face;
import com.amazonaws.services.rekognition.model.FaceRecord;
import com.amazonaws.services.rekognition.model.IndexFacesRequest;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.IndexFacesResult;
import com.amazonaws.services.rekognition.model.ListFacesRequest;
import com.amazonaws.services.rekognition.model.ListFacesResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class StoreFaceUtils {

    private static String LOG_TAG = StoreFaceUtils.class.getSimpleName();

    public static void storeFace(String collectionId, String imageName, String bucket) {
        final String attributes = "ALL";
        final int limit = 1;
        ObjectMapper objectMapper = new ObjectMapper();
        AmazonRekognition amazonRekognitionUtils = AmazonRekognitionUtils.getAmazonRekognition();
        Image image = ImageUtils.getImageUtil(bucket, imageName);
        IndexFacesResult indexFacesResult = callIndexFaces(collectionId, imageName, attributes, image, amazonRekognitionUtils);
        List<FaceRecord> faceRecordList = indexFacesResult.getFaceRecords();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(faceRecordList)) {
            for (FaceRecord faceRecord : faceRecordList) {
                //TODO pegar a imagem e retornar
                Log.i(LOG_TAG, String.format("Face detected. Faceid is %s", faceRecord.getFace().getFaceId()));
            }
        }

        ListFacesResult listFacesResult = null;
        String paginationToken = null;

        do {
            if (listFacesResult != null) {
                paginationToken = listFacesResult.getNextToken();
            }
            listFacesResult = callListFaces(collectionId, limit, paginationToken, amazonRekognitionUtils);
            List<Face> faceList = listFacesResult.getFaces();
            if (CollectionUtils.isNotEmpty(faceList)) {
                for (Face face : faceList) {
                    //TODO pegar a imagem e retornar
                    try {
                        Log.i(LOG_TAG, String.format("Face detected. Face value as String: %s", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(face)));
                    } catch (JsonProcessingException ex) {
                        Log.e(LOG_TAG, String.format("storeFace - ex: %s", ex.getMessage()));
                    }
                }
            }
        } while (listFacesResult.getNextToken() != null);
    }

    private static IndexFacesResult callIndexFaces(
            String collectionId, String externalImageId,
            String attributes, Image image, AmazonRekognition amazonRekognition) {
        IndexFacesRequest indexFacesRequest = new IndexFacesRequest()
                .withImage(image)
                .withCollectionId(collectionId)
                .withExternalImageId(externalImageId)
                .withDetectionAttributes(attributes);
        return amazonRekognition.indexFaces(indexFacesRequest);
    }

    private static ListFacesResult callListFaces(
            String collectionId, int limit,
            String paginationToken, AmazonRekognition amazonRekognition) {
        ListFacesRequest listFacesRequest = new ListFacesRequest()
                .withCollectionId(collectionId)
                .withMaxResults(limit)
                .withNextToken(paginationToken);
        return amazonRekognition.listFaces(listFacesRequest);
    }
}
