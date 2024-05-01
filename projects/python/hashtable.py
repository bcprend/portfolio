"""
A.  Develop a hash table, without using any additional libraries or classes, that has an insertion function that
takes the package ID as input and inserts each of the following data components into the hash table:
•   delivery address
•   delivery deadline
•   delivery city
•   delivery zip code
•   package weight
•   delivery status (i.e., at the hub, en route, or delivered), including the delivery time
"""
"""
Code obtained from:
Tepe, Cemal. "Let's go Hashing." C950. 2023, November 10
"""

class HashTable:
    # Constructor, defaults to 10 buckets
    # Assigns each bucket with an empty list
    def __init__(self, initial_capacity=10):
        self.table = []
        for i in range(initial_capacity):
            self.table.append([])

    # O(N) worst case if all keys hash to same bucket
    # Accepts the package ID as input and inserts the package object, which contains the data components
    def insert(self, key, item):
        # Determine target bucket
        bucket = int(key) % len(self.table)
        bucket_list = self.table[bucket]

        # Update item if already exists in bucket
        for kv in bucket_list:
            if kv[0] == key:
                kv[1] = item
                return True

        key_value = [key, item]
        bucket_list.append(key_value)
        return True

    # O(N) worst case all keys hashed to same bucket
    def search(self, key):
        # Determine target bucket
        bucket = int(key) % len(self.table)
        bucket_list = self.table[bucket]

        for kv in bucket_list:
            if kv[0] == key:
                return kv[1]
        return None
