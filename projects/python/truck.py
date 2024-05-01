"""
Represents a truck with attributes including truck ID, delivery queue, location, destination, mileage, and time details.
"""

from datetime import time


class Truck:
    SPEED = 18

    # Constructor
    def __init__(self, truck_id, delivery_queue=None, location=0, destination=0, curr_time=time(0), departure_time=0):
        self._truck_id = truck_id
        self._delivery_queue = delivery_queue
        self._curr_location_index = location
        self._destination = destination
        self._curr_time = curr_time
        self._departure_time = departure_time
        self._distance_traveled = 0

    def truck_id(self):
        return self._truck_id

    def curr_location_index(self, t=None):
        if t is not None: self._curr_location_index = t
        return self._curr_location_index

    def distance_traveled(self, t=None):
        if t: self._distance_traveled = round(self._distance_traveled + t, 1)
        return self._distance_traveled

    def delivery_queue(self, t=None):
        if t: self._delivery_queue = t
        return self._delivery_queue

    # Updates truck's clock by adding t seconds
    def curr_time(self, t=None):
        if t:
            # Convert the current time and t to seconds
            curr_time_seconds = self._curr_time.hour * 3600 + self._curr_time.minute * 60 + self._curr_time.second
            updated_time_seconds = curr_time_seconds + t

            # Calculate the updated time components
            hours = updated_time_seconds // 3600
            minutes = (updated_time_seconds % 3600) // 60
            seconds = updated_time_seconds % 60

            # Create a new time object with the updated time
            self._curr_time = time(int(hours), int(minutes), int(seconds))

        return self._curr_time

    # Sets departure time to t. Resets truck clock to departure time
    def departure_time(self, t=None):
        if t:
            hours = t // 3600
            minutes = (t % 3600) // 60
            seconds = t % 60

            self._departure_time = time(int(hours), int(minutes), int(seconds))
            self._curr_time = self._departure_time

        return self._departure_time
