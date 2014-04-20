package com.promenadevt.library;

import java.util.Locale;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;

public class Constants {
	//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	// This sample App is for demonstration purposes only.
	// It is not secure to embed your credentials into source code.
	// DO NOT EMBED YOUR CREDENTIALS IN PRODUCTION APPS.
	// We offer two solutions for getting credentials to your mobile App.
	// Please read the following article to learn about Token Vending Machine:
	// * http://aws.amazon.com/articles/Mobile/4611615499399490
	// Or consider using web identity federation:
	// * http://aws.amazon.com/articles/Mobile/4617974389850313
	//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	public static final String ACCESS_KEY_ID = "";
	public static final String SECRET_KEY = "";

	public String PROP_ID;// = "picture-bucket";
	public String ROOM_ID;// = "NameOfThePicture";
	
	public Constants(String bucket, String name){
		PROP_ID = bucket;
		ROOM_ID = "room"+name;
	}


	public String getPictureBucket() {
		return ("promenadevt-1").toLowerCase(Locale.US);
	}

}
