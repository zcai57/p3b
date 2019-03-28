import java.lang.reflect.Array;

////////////////////////////////////////////////////////////////////////////////
//
// Title: BST
// File: BST.java
// Course: cs400, Spring, 2019
//
// Author: Zhuoliang Cai
// Email: zcai57@wisc.edu
// Lecturer's Name: Andrew Kummel
//
////////////////////////////////////////////////////////////////////////////////

// TODO: comment and complete your HashTableADT implementation
// DO ADD UNIMPLEMENTED PUBLIC METHODS FROM HashTableADT and DataStructureADT TO YOUR CLASS
// DO IMPLEMENT THE PUBLIC CONSTRUCTORS STARTED
// DO NOT ADD OTHER PUBLIC MEMBERS (fields or methods) TO YOUR CLASS
//
// TODO: implement all required methods
//
// TODO: I choose the bucket collision resolution. I constructs my hashTable as an array of linkList.
//
// TODO: explain your hashing algorithm here 
// NOTE: you are not required to design your own algorithm for hashing,
//       since you do not know the type for K,
//       you must use the hashCode provided by the <K key> object
//       and one of the techniques presented in lecture
//
public class HashTable<K extends Comparable<K>, V> implements HashTableADT<K, V> {
	
	/**
	 * A number that will cause resize and rehash when loadFactor is equal or bigger than
	 * this number
	 */
	private double loadFactorThreshold;
	
	/**
	 * hashtable: an array of LinkNode
	 */
	private linkNode[] hashTable;
	
	/**
	 * Number of element that can be stored in the array
	 */
	private int capacity;
	
	/**
	 * Number of key and value pair in the hashTable
	 */
	private int numKeys;
	
	/**
	 * Default HashTable constructor
	 */
	public HashTable() {
		this.loadFactorThreshold = 0.75;
		this.capacity = 11;
		hashTable = new linkNode[capacity];
	}
	
	/**
	 * Constructor that take capacity and initial loadFactorThreshold as parameters
	 * @param initialCapacity
	 * @param loadFactorThreshold
	 */
	public HashTable(int initialCapacity, double loadFactorThreshold) {

		hashTable = new linkNode[initialCapacity];
		this.loadFactorThreshold = loadFactorThreshold;
		this.capacity = initialCapacity;
	}
	
	/**
	 * Class of linkNode used to solve collision in the hashTable.
	 * Each linkNode have a key, a value and a pointer to the nextNode.
	 * @author Zhuoliang
	 *
	 * @param <K>
	 * @param <V>
	 */
	private class linkNode<K extends Comparable<K>,V> {
		private K key;
		private V value;
		private linkNode<K,V> nextNode;
		public linkNode(K key, V value) {
			this.key = key;
			this.value = value;
			this.nextNode = null;
		}
		public void setNextNode(linkNode<K,V> node){
			this.nextNode = node;
		}
		public linkNode<K,V> getNextNode(){
			return nextNode;
		}
		public K getKey() {
			return key;
		}
		public void setKey(K key) {
			this.key = key;
		}
	}
	
	/**
	 * Add the key,value pair to the data structure and increase the number of keys.
	 *  If key is null, throw IllegalNullKeyException;
	 *  If key is already in data structure, throw DuplicateKeyException();
	 */
	@Override
	public void insert(K key, V value) throws IllegalNullKeyException, DuplicateKeyException {
		if(key == null) {
			throw new IllegalNullKeyException();
		}
		if(contains(key)) {
			throw new DuplicateKeyException();
		}
		if(hashTable[getTableIndex(key)] == null) {// if element in the array is null
			hashTable[getTableIndex(key)] = new linkNode(key, value);
			numKeys++;
			if(getLoadFactor() >= loadFactorThreshold) {
				rehash();
			}
			
			
		}else {//insert in the linkList
			linkNode<K,V> curNode = hashTable[getTableIndex(key)];
			while(curNode.nextNode != null) {
				curNode = curNode.getNextNode();
			}
			curNode.setNextNode(new linkNode(key, value));
			numKeys++;
			if(getLoadFactor() >= loadFactorThreshold) {
				rehash();
			}
			
			
		}
	}
	
	
	/**
	 * Contains method determines whether certain is already in the array
	 * Parameter：key
	 */
	public boolean contains(K key) throws IllegalNullKeyException{
		if(key == null)
			throw new IllegalNullKeyException();
		int keyIndex = getTableIndex(key);
		if(hashTable[keyIndex] == null) {//array[key] element doesn't exist
			return false;
		}else {
			if(hashTable[keyIndex].getKey().compareTo(key) != 0) {//if not the first linkNode
				linkNode<K,V> curNode = hashTable[keyIndex];
				while(hashTable[keyIndex]!= null) {
					if(curNode.getKey().compareTo(key) == 0) {
						return true;//find key in the linklist of the array[key]
					}
					curNode = curNode.getNextNode();
				}
				return false;//traverse through the end of linklist and didn't find key 
			}
			return true;//find key at the first linknode of the array[key]
		}
	}
	
	/**
	 * Helper method used to get a key's index in the hash table
	 * @param key
	 * @return
	 */
	public int getTableIndex(K key) {
		return (((key).hashCode() % capacity)+capacity)%capacity;
	}
	
	/**
	 * If key is found, 
	 *    remove the key,value pair from the data structure
	 *    decrease number of keys
	 *    return true
	 *  If key is null, throw IllegalNullKeyException
	 *  If key is not found, return false
	 */
	@Override
	public boolean remove(K key) throws IllegalNullKeyException {
		if(key == null)
			throw new IllegalNullKeyException();
		int keyIndex = getTableIndex(key);
		if(hashTable[keyIndex] == null) {
			return false;
		}else {
			linkNode<K,V> curNode = hashTable[keyIndex];
			if(curNode.key.compareTo(key) != 0) {
				while(curNode.nextNode != null) {
					if(curNode.nextNode.key.compareTo(key) == 0) {
						curNode.setNextNode(curNode.getNextNode().getNextNode());
						numKeys--;
						return true;
					}
					curNode = curNode.getNextNode();
				}
				return false;
			}
			if(hashTable[keyIndex].nextNode == null) {//if the linklist only have one node, put the list to null
				numKeys--;
				hashTable[keyIndex] = null;
				return true;
			}else {// if linklist have multiple nodes and want to remove the first node
				hashTable[keyIndex] = curNode.getNextNode();
				numKeys--;
				return true;
			}
		}
	}
	
	/**
	 * Returns the value associated with the specified key
	 * Does not remove key or decrease number of keys
	 * If key is null, throw IllegalNullKeyException 
	 * If key is not found, throw KeyNotFoundException().
	 */
	@Override
	public V get(K key) throws IllegalNullKeyException, KeyNotFoundException {
		if(key == null)
			throw new IllegalNullKeyException();
		int keyIndex = getTableIndex(key);
		if(hashTable[keyIndex] == null) {
			throw new KeyNotFoundException();
		}else {
			linkNode<K,V> curNode = hashTable[keyIndex];
			if(curNode.key.compareTo(key) != 0) {
				while(curNode != null) {
					if(curNode.key.compareTo(key)==0) {
						return curNode.value;//return value of the node that matched the key
					}
					curNode = curNode.getNextNode();
				}
				throw new KeyNotFoundException();
			}
			return curNode.value;// return value of the first linkNode of the linkList
		}
	}
	/**
	 * Helper method that returns number of keys that is inserted into the table
	 */
	@Override
	public int numKeys() {
		return numKeys;
	}
	
	private void rehash() throws IllegalNullKeyException, DuplicateKeyException {
		int newTableSize = 2*capacity + 1;
		linkNode[] tempTable = hashTable;
		hashTable = new linkNode[newTableSize];
		int oldSize = capacity;
		capacity = newTableSize;
		numKeys = 0;
			
		for(int i = 0; i < oldSize; i++) {
			if(tempTable[i] != null) {// if element is found
				linkNode<K,V> curNode = tempTable[i];
				while(curNode != null) {//element have more than one node
					insert(curNode.key, curNode.value);
					curNode = curNode.getNextNode();
				}
			}
			//element is null
		}	
	}
	

	/** 
	 * Returns the load factor threshold that was 
	 * passed into the constructor when creating 
	 * the instance of the HashTable.
	 * When the current load factor is greater than or 
	 * equal to the specified load factor threshold,
	 * the table is resized and elements are rehashed.
	 * @throws DuplicateKeyException 
	 * @throws IllegalNullKeyException 
     */
	@Override
	public double getLoadFactorThreshold() {
		
		return loadFactorThreshold;
	}
	
	/**
	 * Helper method that compute the loadFactor of the table
	 * LoadFactor = elements in the table / size of the table
	 */
	@Override
	public double getLoadFactor() {
		return numKeys/(double)capacity;
	}

	/**
	 * Helper method that get the size of the hashTable array
	 * @return Size of the table
	 */
	@Override
	public int getCapacity() {
		return capacity;
	}
	
	/**
	 * Returns the collisionResolution number
	 */
	@Override
	public int getCollisionResolution() {
		return 5;
	}
	

	// TODO: add all unimplemented methods so that the class can compile




		
}
