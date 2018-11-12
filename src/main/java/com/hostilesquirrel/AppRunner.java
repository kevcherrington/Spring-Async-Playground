package com.hostilesquirrel;

import com.hostilesquirrel.model.User;
import com.hostilesquirrel.service.GitHubLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class AppRunner implements CommandLineRunner {
	
	private static final Logger logger = LoggerFactory.getLogger( AppRunner.class );
	
	private final GitHubLookupService gitHubLookupService;
	
	public AppRunner( GitHubLookupService gitHubLookupService ) {
		this.gitHubLookupService = gitHubLookupService;
	}
	
	@Override
	public void run( String... args ) throws Exception {
		completesWithFirstCompletedFuture();
	}
	
	private void waitForAllToComplete() throws Exception {
		// Start the clock
		long start = System.currentTimeMillis();
		
		// Kick of multiple, asynchronous lookups
		CompletableFuture<User> page1 = gitHubLookupService.findUser("PivotalSoftware");
		CompletableFuture<User> page2 = gitHubLookupService.findUser("CloudFoundry");
		CompletableFuture<User> page3 = gitHubLookupService.findUser("Spring-Projects");
		
		// Wait until they are all done
		CompletableFuture.allOf(page1,page2,page3).join();
		
		// Print results, including elapsed time
		logger.info("Elapsed time: " + (System.currentTimeMillis() - start));
		logger.info("--> " + page1.get());
		logger.info("--> " + page2.get());
		logger.info("--> " + page3.get());
	}
	
	private void completesWithFirstCompletedFuture() throws Exception {
		// start the clock
		long start = System.currentTimeMillis();
		
		// Kick off multiple async lookups
		CompletableFuture< User > page1 = gitHubLookupService.findUser( "PivotalSoftware" );
		CompletableFuture< User > page2 = gitHubLookupService.findUser( "CloudFoundry" );
		CompletableFuture< User > page3 = gitHubLookupService.findUser( "Spring-Projects" );
		
		// Wait for all to be done
		CompletableFuture cfs = CompletableFuture.anyOf( page1, page2, page3 )
		                                         .whenComplete( ( res, th ) -> {
			                                         if( th == null ) {
				                                         System.err.println( "--> res " + res + "Int Elapsed time: "
					                                                             + ( System.currentTimeMillis() - start ));
			                                         }
		                                         } );
		// Print results, including elapsed time
		System.err.println( "--> " + page1.get() + "PS Elapsed time: " + ( System.currentTimeMillis() - start ) );
		System.err.println( "--> " + page2.get() + "CF Elapsed time: " + ( System.currentTimeMillis() - start ) );
		System.err.println( "--> " + page3.get() + "SP Elapsed time: " + ( System.currentTimeMillis() - start ) );
	}
}
