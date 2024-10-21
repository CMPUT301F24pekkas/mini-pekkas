package com.example.mini_pekkas;

import static org.junit.Assert.assertEquals;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Testing class for the firebase class
 */
public class FirebaseTest {
    private Firebase firebase = new Firebase();

    @Before
    public void makeAdminTestData() {
        Map<String, String> data1 = new HashMap<>();
        Map<String, String> data2 = new HashMap<>();

        // Fill with test data
        data1.put("admin1id", "John Lena");
        data1.put("admin2id", "Zamn Cena");
        data1.put("admin3id", "john Xina");

        data2.put("admin4id", "Xina Lena");
        data2.put("admin5id", "Zamn john");
        data2.put("admin6id", "cena Zamn");

        firebase.getDb().collection("testAdmins").document("doc1").set(data1);
        firebase.getDb().collection("testAdmins").document("doc2").set(data2);

    }

    /**
     * Test if the document was made
     */
    @Test
    public void testExist() {
        DocumentReference docRef = firebase.getDb().document("testAdmins");
        assertEquals(2, docRef.);
    }

    @After
    /**
     * Delete the test cases/clean the database
     */
    public void cleanUp() {
        firebase.getDb().document("testAdmins").delete();
    }
}
