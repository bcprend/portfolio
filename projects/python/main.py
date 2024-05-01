"""
Benjamin Prendergast, Student ID: 005322493
C950 Performance Assessment
Task 2: WGUPS Routing Program Implementation
"""

import csv
import os
from datetime import time

from graph import Graph
from hashtable import HashTable
from locations import Locations
from pack import Pack
from truck import Truck

'''
B. Develop a look-up function that takes the package ID as input and returns each of the following corresponding data 
components:
•   delivery address
•   delivery deadline
•   delivery city
•   delivery zip code
•   package weight
•   delivery status (i.e., at the hub, en route, or delivered), including the delivery time
'''


# O(N)
# Takes the package ID as input and returns each of the data components
def lookup(package_id, package_hash):
    p = package_hash.search(package_id)

    if p:
        package_address = p.address()
        package_deadline = p.deadline()
        package_city = p.city()
        package_zip = p.zipcode()
        package_weight = p.weight()
        package_status = p.status()
        package_delivery_truck = p.delivery_truck()
        package_delivery_time = p.delivery_time()

        return (f'ID: {package_id}, Address: {package_address}, City: {package_city}, Zip: {package_zip}, '
                f'Weight: {package_weight}, Deadline: {package_deadline}, Delivery Status: {package_status} '
                f'by {package_delivery_truck} at {package_delivery_time}')
    else:
        return None


# O(N)
# Instantiates the hash table, reads the package CSV file, creates package objects, and inserts them into hash table
def read_package_csv(file_path):
    package_hash = HashTable()  # Hash table data structure per assignment requirements
    package_id_list = []  # list for tracking package IDs

    with open(file_path, 'r') as package_file:
        package_reader = csv.reader(package_file)

        next(package_reader)  # Skip the header

        # Construct package objects with appropriate status and insert to hash table
        for row in package_reader:
            pack_id = int(row[0])
            address = row[1]
            city = row[2]
            state = row[3]
            zipcode = row[4]
            deadline = row[5]
            weight = row[6]
            notes = row[7]
            if 'Delayed' in notes:
                status = 'Delayed en route to Hub'
            else:
                status = 'At Hub'
            package = Pack(pack_id, address, city, state, zipcode, deadline, weight, notes, status)

            package_hash.insert(pack_id, package)
            package_id_list.append(pack_id)

        return package_hash, package_id_list


# O(N^2)
# Instantiates the distance graph and location dictionary
# Reads the distance CSV and populates the distance graph and location dictionary
def read_distance_csv(file_path):
    distance_graph = Graph()
    locations_dict = Locations()

    with open(file_path, 'r') as distance_file:
        distance_reader = csv.reader(distance_file)

        next(distance_reader)  # Skips header

        # Iterate through file and add all edges to graph
        for row_index, row in enumerate(distance_reader):
            locations_dict.add_location(row_index, row[0])  # Adds value in first column to location dictionary
            for cell_index, cell in enumerate(row[1:]):
                if cell:
                    distance_graph.add_edge(row_index, cell_index, cell)

    return distance_graph, locations_dict


# O(1)
# Updates the delivery information for a given package
def update_package_destination(package_id, address, city, state, zipcode, package_hash):
    this_package = package_hash.search(package_id)

    this_package.address(address)
    this_package.city(city)
    this_package.state(state)
    this_package.zipcode(zipcode)


# O(N)
# Adds packages in the given list to the given truck and updates package status
def load_truck(truck, package_list, package_hash):
    if truck.curr_location_index() == 0:  # Verify truck is currently at hub before loading
        truck.delivery_queue(package_list)

        # Iterates through package list and updates package delivery truck and departure time
        for p_id in package_list:
            p = package_hash.search(p_id)
            p.departure_time(truck.departure_time())
            p.delivery_truck(truck.truck_id())
    else:
        print("Truck must return to Hub before loading packages")


# O(N^2)
# Applies the nearest neighbor algorithm to a given package list and returns an optimized list
def sort_packages_nn(packages_to_sort, package_hash, locations_dict, distance_graph):
    curr_location_index = 0  # Starting from Hub
    remaining_packages = set(packages_to_sort)  # A set to avoid loop issues due to removing list elements in loop
    sorted_packages = []

    while remaining_packages:
        closest_neighbor = None
        closest_location_index = None
        shortest_distance = float('inf')

        for package_id in set(remaining_packages):  # Iterate through all package IDs in remaining_packages
            # Store the details of the current package
            neighbor = package_hash.search(package_id)
            neighbor_address = neighbor.address()
            neighbor_location_index = locations_dict.get_location_index(neighbor_address)

            # Check the distance between the current location and the current package
            distance = distance_graph.get_distance(curr_location_index, neighbor_location_index)

            # If a shorter distance is found, set closest neighbor and shortest distance to current package
            if distance < shortest_distance:
                shortest_distance = distance
                closest_neighbor = package_id
                closest_location_index = neighbor_location_index

        # Add closest neighbor to sorted_packages list, remove it from remaining list, and update current location
        sorted_packages.append(closest_neighbor)
        remaining_packages.remove(closest_neighbor)
        curr_location_index = closest_location_index

    return sorted_packages


# O(N^2)
# Calls the sorting algorithm on loaded packages to optimize the delivery route
# Iterates through the sorted package list and delivers each package while tracking elapsed time and distance traveled
# Updates package status
def make_deliveries(truck, package_hash, locations_dict, distance_graph):
    if truck.delivery_queue():
        # Call sorting algorithm to sort packages
        optimized_queue = sort_packages_nn(truck.delivery_queue(), package_hash, locations_dict, distance_graph)

        # Iterate through each package
        for package_id in optimized_queue:
            curr_location = truck.curr_location_index()  # Get truck's current location index
            curr_package = package_hash.search(package_id)  # Get package from hash table by searching for package ID
            destination = locations_dict.get_location_index(
                curr_package.address())  # Set destination from package address

            distance_to_next = distance_graph.get_distance(curr_location, destination)  # Get distance to destination
            truck.distance_traveled(distance_to_next)  # Add distance to truck mileage

            travel_time = round((3600 * distance_to_next / truck.SPEED), 2)  # Get travel time in seconds
            truck.curr_time(travel_time)  # Add time traveled to truck's clock

            curr_package.delivery_time(truck.curr_time())  # Assign package delivery time

            truck.curr_location_index(destination)  # Update truck's current location index


# O(1)
# Determines which truck completed deliveries first and returns it to hub to be reloaded
def return_to_hub(t1, t2, distance_graph):
    # Determines which truck finished deliveries first and sets it to the return truck
    if t1.curr_time() < t2.curr_time():
        return_truck = t1
    else:
        return_truck = t2

    curr_location = return_truck.curr_location_index()  # Get truck's current location

    # Calculate time and distance to hub
    distance_to_hub = distance_graph.get_distance(curr_location, 0)
    time_to_hub = round((3600 * distance_to_hub / return_truck.SPEED), 2)

    # Add time and distance to trucks total mileage and elapsed time
    return_truck.distance_traveled(distance_to_hub)
    return_truck.curr_time(time_to_hub)

    # Update truck location
    return_truck.curr_location_index(0)


# O(1)
# Determines the earliest possible time for the final delivery
def determine_final_departure(t1, t2, package_update_time):
    # If truck 1 returns before truck 2, earliest final departure is the later of truck 1's time and package 9's update
    if t1.curr_time() < t2.curr_time():
        earliest_time = max(package_update_time, t1.curr_time())
        return earliest_time, t1
    # If truck 2 returns before truck 1, earliest final departure is the later of truck 2's time and package 9's update
    else:
        earliest_time = max(package_update_time, t2.curr_time())
        return earliest_time, t2


# O(N)
# Checks time of final delivery
# Checks whether any packages were late
# Prints recap of the day
def daily_recap(package_hash, t1, t2):
    print('\n\nHere is a recap of the day:\n')

    # Print distance metrics
    print(f'Truck 1 traveled {t1.distance_traveled()} miles.')
    print(f'Truck 2 traveled {t2.distance_traveled()} miles.')
    print('Total miles traveled today: ', t1.distance_traveled() + t2.distance_traveled())

    # Determine and print final delivery time
    last_delivery = max(t1.curr_time(), t2.curr_time())
    print('\nFinal delivery was completed at ', last_delivery)

    # Creates a list of all packages in hash table
    all_packages = []
    for bucket_list in package_hash.table:
        for kv in bucket_list:
            package_id, package_obj = kv
            all_packages.append((package_id, package_obj))

    late_packages = []  # List to store packages that were delivered late

    # Compares delivery deadline and delivery time for all packages with deadlines
    for package_id, package_obj in all_packages:
        delivery_time = package_obj.delivery_time()
        deadline_string = package_obj.deadline()

        if 'EOD' not in deadline_string:
            hour, minute = map(int, deadline_string.split(':'))
            deadline_time = time(hour, minute)

            # Adds late and undelivered packages to the late packages list
            if deadline_time < delivery_time or delivery_time is None:
                late_packages.append(package_id)


    if late_packages:
        print(f'Unfortunately, packages {late_packages} were delivered late.\n\n')
    else:
        print('All packages were delivered on time.\n\n')


# O(N log N)
# Prints details of all packages at the given time
def all_time_detail(hour, minute, package_hash):
    given_time = time(hour, minute)

    # Creates a list of all packages in hash table
    all_packages = []
    for bucket_list in package_hash.table:
        for kv in bucket_list:
            package_id, package_obj = kv
            all_packages.append((package_id, package_obj))

    all_packages.sort()  # Sorts packages for readability O(N log N) https://www.geeksforgeeks.org/sort-in-python/

    # Report header
    print(
        f'ID|                Address                  |      City       | State|  Zip | Weight | Due By |      Status as of {given_time}      | Notes')

    # Iterates through all packages and print details
    for package_id, package_obj in all_packages:
        print(package_obj.time_detail(given_time))


# O(N)
# Print details of the given package at the given time
def time_detail(package_id, hour, minute, package_hash):
    given_time = time(hour, minute)

    # Report header
    print(
        f'ID|                Address                  |      City       | State | Zip | Weight | Due By |      Status as of {given_time}      | Notes')

    print(package_hash.search(package_id).time_detail(given_time))


# O(1)
# Displays the user interface
def show_menu():
    print(' /$$      /$$  /$$$$$$  /$$   /$$ /$$$$$$$   /$$$$$$  %%%%%%#::%%%%%%%#:-%%%%%%%')
    print('| $$  /$ | $$ /$$__  $$| $$  | $$| $$__  $$ /$$__  $$   %%%%%%%%%%%%%%%%%%%%%#')
    print('| $$ /$$$| $$| $$  \__/| $$  | $$| $$  \ $$| $$  \__/   :.-%%%%%%%%%%%%%%%%..-')
    print('| $$/$$ $$ $$| $$ /$$$$| $$  | $$| $$$$$$$/|  $$$$$$   =%-:%%#%%%.:%%%%%#%%.:%*')
    print('| $$$$_  $$$$| $$|_  $$| $$  | $$| $$____/  \____  $$  %%.%*-%%#%..%%%.%%%%%.%%')
    print('| $$$/ \  $$$| $$  \ $$| $$  | $$| $$       /$$  \ $$  *%:-%=.:%%..%%%%:.*%..%@')
    print('| $$/   \  $$|  $$$$$$/|  $$$$$$/| $$      |  $$$$$$/   #%%..:.....%%:..:..@%@')
    print('|__/     \__/ \______/  \______/ |__/       \______/      -%%%%%%%.%%%%%%%%=')
    print('Welcome, please choose an option                                 %%%%')
    print('1: Print all package status and total mileage')
    print('2: Track specific package')
    print('3: Track all packages at specific time')
    print('4: Exit')


# O(N Log N)
# Handles user input, calling the appropriate function based on user selection.
def handle_menu(user_input, package_hash, t1, t2):
    if user_input == 1:
        daily_recap(package_hash, t1, t2)

    elif user_input == 2:
        user_package = int(input("Enter package ID:"))
        if lookup(user_package, package_hash):
            user_time = input("Enter time in HH:MM format:")
            time_split = user_time.split(':')

            if len(time_split) == 2:
                hours = int(time_split[0])
                minutes = int(time_split[1])

                if 0 <= hours <= 23 and 0 <= minutes <= 59:
                    time_detail(user_package, hours, minutes, package_hash)
                else:
                    print("Invalid time entry")
            else:
                print("Invalid time entry")
        else:
            print("No package with that ID.")

    elif user_input == 3:
        user_time = input("Enter time in HH:MM format:")
        time_split = user_time.split(':')

        if len(time_split) == 2:
            hours = int(time_split[0])
            minutes = int(time_split[1])

            if 0 <= hours <= 23 and 0 <= minutes <= 59:
                all_time_detail(hours, minutes, package_hash)
            else:
                print("Invalid time entry")
        else:
            print("Invalid time entry")

    elif user_input == 4:
        print('Exiting...')
        exit(0)


# O(N Log N)
# Entry point for user interface. Continues interacting with user until exit
def run_interface(package_hash, t1, t2):
    while True:
        show_menu()
        user_input = input("Make Selection: ")

        try:
            menu_choice = int(user_input)
            handle_menu(menu_choice, package_hash, t1, t2)
        except ValueError:
            print('Invalid selection')

        input("Press Enter to return to main menu.")


# O(N^2)
# Main program
def main():
    # Gets directory of main.py
    current_directory = os.path.dirname(__file__)

    # Paths to CSV files
    package_file_path = os.path.join(current_directory, 'data', 'package.csv')
    distance_file_path = os.path.join(current_directory, 'data', 'distance.csv')

    # Reads package information from CSV into hash table
    package_hash, package_id_list = read_package_csv(package_file_path)

    # Reads distance information from CSV into graph and dictionary
    distance_graph, locations_dict = read_distance_csv(distance_file_path)

    # Instantiates truck objects (Since there are only 2 drivers and loading is instant, will only use 2 trucks)
    t1 = Truck('Truck 1')
    t2 = Truck('Truck 2')

    # Manually created package groups
    # Future enhancement: Automate package group assignment based on deadline and special notes
    package_group_one = [4, 7, 10, 13, 14, 15, 16, 19, 20, 21, 29, 30, 34, 37, 39, 40]
    package_group_two = [1, 2, 3, 5, 6, 8, 11, 12, 18, 25, 28, 31, 32, 33, 36, 38]
    package_group_three = [9, 17, 22, 23, 24, 26, 27, 35]

    # Sets truck 1 departure time and loads it with the first package group
    t1.departure_time(28800)
    load_truck(t1, package_group_one, package_hash)

    # Optimizes delivery route and delivers packages on truck 1
    make_deliveries(t1, package_hash, locations_dict, distance_graph)

    # Sets truck 2 departure time and loads it with the second package group
    t2.departure_time(32700)  # Set departure time
    load_truck(t2, package_group_two, package_hash)

    # Optimizes delivery route and delivers packages on truck 1
    make_deliveries(t2, package_hash, locations_dict, distance_graph)

    # Determines which truck finished its deliveries first and returns that truck to hub to be reloaded
    return_to_hub(t1, t2, distance_graph)

    # Updates address for package #9 at 10:20 per assignment instructions
    update_package_destination(9, '410 S State St', 'Salt Lake City', 'UT', '84111', package_hash)
    all_packages_ready = time(10, 20)

    # Calculates earliest departure time for third delivery
    final_time, final_truck = determine_final_departure(t1, t2, all_packages_ready)

    # Sets final truck departure time and loads it with the third package group
    final_truck.departure_time(final_time.hour * 3600 + final_time.minute * 60 + final_time.second)
    load_truck(final_truck, package_group_three, package_hash)

    # Optimizes delivery route and delivers packages on final truck
    make_deliveries(final_truck, package_hash, locations_dict, distance_graph)

    # Runs user interface
    run_interface(package_hash, t1, t2)


# Press the green button in the gutter to run the script.
if __name__ == '__main__': main()
