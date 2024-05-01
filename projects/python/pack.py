"""
Represents a package with attributes including package ID, address, status, and delivery details.
"""


class Pack:
    # constructor
    def __init__(self, pack_id, address, city, state, zipcode, deadline, weight, notes, status):
        self._pack_id = pack_id
        self._address = address
        self._city = city
        self._state = state
        self._zipcode = zipcode
        self._deadline = deadline
        self._weight = weight
        self._notes = notes
        self._status = status
        self._departure_time = None
        self._delivery_truck = None
        self._delivery_time = None

    def pack_id(self, t=None):
        if t: self._pack_id = t
        return self._pack_id

    def address(self, t=None):
        if t: self._address = t
        return self._address

    def city(self, t=None):
        if t: self._city = t
        return self._city

    def state(self, t=None):
        if t: self._state = t
        return self._state

    def zipcode(self, t=None):
        if t: self._zipcode = t
        return self._zipcode

    def deadline(self, t=None):
        if t: self._deadline = t
        return self._deadline

    def weight(self, t=None):
        if t: self._weight = t
        return self._weight

    def notes(self, t=None):
        if t: self._notes = t
        return self._notes

    def status(self, t=None):
        if t: self._status = t
        return self._status

    def departure_time(self, t=None):
        if t: self._departure_time = t
        return self._departure_time

    def delivery_truck(self, t=None):
        if t: self._delivery_truck = t
        return self._delivery_truck

    def delivery_time(self, t=None):
        if t: self._delivery_time = t
        return self._delivery_time

    def __str__(self):
        if self._status == 'Delivered':
            return f"Package ID: {self._pack_id}, Delivery Deadline: {self._deadline}, Status: {self._status} at {self._delivery_time} on {self._delivery_truck}"
        else:
            return f"Package ID: {self._pack_id}, Delivery Deadline: {self._deadline}, Status: {self._status}"

    # Prints package details as of given time
    def time_detail(self, given_time):
        if given_time <= self._departure_time:
            return (f"{self._pack_id:2}| {self._address:40}| {self._city:16}| {self._state:5}| {self._zipcode:5}| "
                    f"{self._weight:7}| {self._deadline:7}| {self._status:32}| {self._notes}")
        elif self._departure_time < given_time < self._delivery_time:
            return (f"{self._pack_id:2}| {self._address:40}| {self._city:16}| {self._state:5}| {self._zipcode:5}| "
                    f"{self._weight:7}| {self._deadline:7}| Out for delivery on {self._delivery_truck}     | {self._notes}")
        else:
            return (f"{self._pack_id:2}| {self._address:40}| {self._city:16}| {self._state:5}| {self._zipcode:5}| "
                    f"{self._weight:7}| {self._deadline:7}| Delivered by {self._delivery_truck} at {self._delivery_time}| {self._notes}")
