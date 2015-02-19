package edu.und.cs.com.heart_monitor;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.PUT;
/**
 * Created by Andrew on 2/18/2015.
 */
/**
 * Definition of REST service available in BITalino Server.
 */
public interface ReadingService {

    @PUT("/")
    Response uploadReading(@Body BITalinoReading reading);

}

