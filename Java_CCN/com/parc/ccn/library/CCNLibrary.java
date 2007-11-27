package com.parc.ccn.library;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;

import com.parc.ccn.data.CCNBase;
import com.parc.ccn.data.CompleteName;
import com.parc.ccn.data.ContentName;
import com.parc.ccn.data.security.ContentAuthenticator;
import com.parc.ccn.data.security.KeyLocator;
import com.parc.ccn.data.security.PublisherID;

/**
 * Higher-level interface to CCNs.
 * @author smetters
 * 
 * <META> tag under which to store metadata (either on name or on version)
 * <V> tag under which to put versions
 * n/<V>/<number> -> points to header
 * <B> tag under which to put actual fragments
 * n/<V>/<number>/<B>/<number> -> fragments
 * n/<latest>/1/2/... has pointer to latest version
 *  -- use latest to get header of latest version, otherwise get via <v>/<n>
 * configuration parameters:
 * blocksize -- size of chunks to fragment into
 * 
 * get always reconstructs fragments and traverses links
 * can getLink to get link info
 *
 */
public interface CCNLibrary extends CCNBase {

	public void put(ContentName name, byte [] contents) throws SignatureException;

	/**
	 * Publish a piece of content under a particular identity.
	 * @param name
	 * @param contents
	 * @param publisher selects one of our identities to publish under
	 * @throws SignatureException 
	 */
	public void put(ContentName name, byte [] contents, PublisherID publisher) throws SignatureException;
	
	// internal functions about fragmentation - may be exposed, or in std impl
	
	public void newVersion(ContentName name, byte [] contents) throws SignatureException;
	public void newVersion(ContentName name, int version, byte [] contents) throws SignatureException;
	public void newVersion(ContentName name, byte [] contents, PublisherID publisher) throws SignatureException;
	public void newVersion(ContentName name, int version, byte [] contents, PublisherID publisher) throws SignatureException;
	public void newVersion(ContentName name, int version, byte [] contents,
			PublisherID publisher, KeyLocator locator,
			PrivateKey signingKey) throws SignatureException, InvalidKeyException, NoSuchAlgorithmException;
	public int getLatestVersion(ContentName name, PublisherID publisher);
	public int getVersion(ContentName name);
	
	// TODO islink
	// TODO getlink ( no deref)
	public void link(ContentName src, ContentName dest) throws SignatureException;
	public void link(ContentName src, ContentName dest, ContentAuthenticator destAuthenticator) throws SignatureException;
	public void link(ContentName src, ContentName dest, PublisherID publisher) throws SignatureException;
	public void link(ContentName src, ContentName dest, ContentAuthenticator destAuthenticator, PublisherID publisher) throws SignatureException;
	public void link(ContentName src, ContentName dest,
			ContentAuthenticator destAuthenticator, 
			PublisherID publisher, KeyLocator locator,
			PrivateKey signingKey) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException;
	
	public void addCollection(ContentName name, ContentName [] contents) throws SignatureException;
	public void addCollection(ContentName name, CompleteName [] contents) throws SignatureException;
	public void addCollection(ContentName name, ContentName [] contents, PublisherID publisher) throws SignatureException;
	public void addCollection(ContentName name, CompleteName [] contents, PublisherID publisher) throws SignatureException;
	public void addCollection(ContentName name, 
			ContentName[] contents,
			PublisherID publisher, KeyLocator locator,
			PrivateKey signingKey) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException;
	public void addCollection(ContentName name, 
			CompleteName[] contents,
			PublisherID publisher, KeyLocator locator,
			PrivateKey signingKey) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException;
	
	/**
	 * Use the same publisherID that we used originally.
	 */
	public void addToCollection(ContentName name, ContentName [] additionalContents);
	public void addToCollection(ContentName name, CompleteName [] additionalContents);
	public void removeFromCollection(ContentName name, ContentName [] additionalContents);
	public void removeFromCollection(ContentName name, CompleteName [] additionalContents);
}
