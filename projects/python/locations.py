"""
Manages location index-address mappings for address lookups
"""

class Locations:
    def __init__(self):
        self._locations_dict = {}

    # O(1)
    def add_location(self, index, address):
        self._locations_dict[index] = address

    # O(N)
    # Returns the index of a given address
    def get_location_index(self, address):
        for k, v in self._locations_dict.items():
            if v == address:
                return k
        return None

