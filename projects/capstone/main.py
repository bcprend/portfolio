import csv
import os
import re
import shutil
import webbrowser
import joblib
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import tkinter as tk
from tkinter import filedialog, messagebox, simpledialog
from tkinter.scrolledtext import ScrolledText
from matplotlib.ticker import ScalarFormatter
from nltk.corpus import stopwords
from sklearn.metrics import confusion_matrix, accuracy_score, classification_report, ConfusionMatrixDisplay
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.feature_extraction.text import TfidfVectorizer, CountVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.utils import resample

ADMIN_PASSWORD = '1234$'
DATASET_FILE_PATH = 'data/dataset.csv'
TFIDF_VECTORIZER_PATH = 'models/tfidf_vectorizer.pkl'
LOGISTIC_REGRESSION_MODEL_PATH = 'models/logistic_regression_model.pkl'
DATA_PLOTS_IMAGE_PATH = 'data/data_exploration_plots.png'
TRANSCRIPT_DIRECTORY = 'transcripts'


# Returns vectorizer and regression model if they already exist
def load_models():
    if os.path.exists(TFIDF_VECTORIZER_PATH) and os.path.exists(LOGISTIC_REGRESSION_MODEL_PATH):
        tfidf_vectorizer = joblib.load(TFIDF_VECTORIZER_PATH)
        logistic_regression_model = joblib.load(LOGISTIC_REGRESSION_MODEL_PATH)
        return tfidf_vectorizer, logistic_regression_model
    else:
        return None, None


# Downsamples the majority class of the given dataframe and returns a balanced dataframe
def balance_data(df):
    # Determine which class is the majority class
    majority_class = df['classification'].value_counts().idxmax()

    # Separate majority and minority classes
    df_majority = df[df['classification'] == majority_class]
    minority_class = 1 if majority_class == 0 else 0
    df_minority = df[df['classification'] == minority_class]

    # Downsample majority class
    df_majority_downsampled = resample(df_majority, replace=False, n_samples=len(df_minority), random_state=22)

    # Combine minority class with downsampled majority class
    df_balanced = pd.concat([df_majority_downsampled, df_minority])

    return df_balanced


def clean_text(text):
    # Convert text to lowercase
    text = text.lower()

    # Remove special characters
    text = re.sub(r'[^a-zA-Z\s]', '', text)

    # Tokenize the text
    words = text.split()

    # Remove stopwords except 'no' and 'not'
    stop_words = set(stopwords.words('english'))
    cleaned_words = [word for word in words if word not in stop_words or word in ['no', 'not']]

    # Join the words back into a single string
    text = ' '.join(cleaned_words)

    return text


# Loads and preprocesses the dataset and returns a formatted DataFrame
def load_preprocess_dataset():
    # Read CSV into a DataFrame using specified column names
    column_names = ['post-text', 'classification']
    df = pd.read_csv(DATASET_FILE_PATH, names=column_names)

    # Print DataFrame details
    print(f'\nDataset Dimensions: {df.shape}\n')
    print(df['classification'].value_counts())

    # Convert 'suicide' and 'non-suicide' labels to 1 and 0 respectively for ML model usage
    df['classification'] = np.where(df['classification'] == 'suicide', 1, 0)

    # Determine class balance ratio
    positive_counts = df['classification'].value_counts().get(1, 0)
    negative_counts = df['classification'].value_counts().get(0, 0)

    classification_ratio = positive_counts / negative_counts

    # Balance DataFrame
    if not (.7 <= classification_ratio <= 1.3):
        print("Dataset beyond classification balance threshold. Balancing data.")
        df = balance_data(df)

    # Replace NaN values in 'post-text' column
    df['post-text'].fillna('', inplace=True)

    df['cleaned-text'] = df['post-text'].apply(clean_text)

    df.to_csv('data/cleaned_dataset.csv', index=False)

    return df


# Uses the given dataframe and confusion matrix to generate data and model visualizations
def generate_data_plots(cm, df):
    print('Generating data plots...')
    # Function to create and display data exploration plots
    fig, axes = plt.subplots(2, 2, figsize=(12, 8))

    # Plot 1: Class Distribution
    class_counts = df['classification'].value_counts()
    axes[0, 0].pie(class_counts, labels=['Non-suicidal', 'Suicidal'], autopct='%1.1f%%', startangle=140)
    axes[0, 0].set_title('Dataset Class Distribution')

    # Plot 2: Text Length
    # Distribution
    df['Text Length'] = df['cleaned-text'].apply(len)
    df['Text Length'].plot(kind='hist', bins=200, ax=axes[0, 1])

    # Average
    average_text_length = df['Text Length'].mean()
    axes[0, 1].axvline(x=average_text_length, color='r', linestyle='--', label='Average Text Length')

    # Plot Settings
    axes[0, 1].set_title('Text Length Distribution')
    axes[0, 1].set_xlabel('Text Length')
    axes[0, 1].set_ylabel('Count')
    axes[0, 1].set_xlim(0, 7500)
    axes[0, 1].legend()

    # Plot 3: Confusion Matrix
    disp = ConfusionMatrixDisplay(confusion_matrix=cm, display_labels=['Non-suicidal', 'Suicidal'])
    disp.plot(ax=axes[1, 0], cmap='Blues')
    # Plot Settings
    axes[1, 0].set_title('Confusion Matrix')
    axes[1, 0].set_xlabel('Predicted')
    axes[1, 0].set_ylabel('Actual')

    # Plot 4: Word Frequency
    vectorizer = CountVectorizer()

    # Filter DataFrame to include only entries where classification is '1'
    filtered_df = df[df['classification'] == 1]

    ft = vectorizer.fit_transform(filtered_df['cleaned-text'])
    word_frequencies = ft.sum(axis=0)
    words = vectorizer.get_feature_names_out()

    # Create a DataFrame to store word frequencies
    word_freq_df = pd.DataFrame({'Word': words, 'Frequency': word_frequencies.tolist()[0]})

    # Sort the DataFrame by word frequency in descending order
    word_freq_df = word_freq_df.sort_values(by='Frequency', ascending=False)

    # Plot the word frequency graph
    ax = axes[1, 1]
    ax.bar(word_freq_df['Word'][:20], word_freq_df['Frequency'][:20])

    # Y-axis formatting
    ax.yaxis.set_major_formatter(ScalarFormatter(useMathText=True))
    ax.ticklabel_format(style='plain', axis='y')  # Ensure plain text format
    ax.tick_params(axis='y', rotation=30)

    ax.set_xlabel('Word')
    ax.set_ylabel('Frequency')
    ax.set_title('Suicidality: Most Common Words')
    ax.tick_params(axis='x', rotation=45)

    plt.tight_layout()
    plt.savefig(DATA_PLOTS_IMAGE_PATH)  # Save the plots as an image
    plt.close()
    print('Plot generation complete.')


# Calls a method to generate the DataFrame, splits the dataset, creates and trains the vectorizer and regression model
# Calls a method to generate data plots
# Saves the vectorizer and regression model, so they don't need to be regenerated on each run
# Evaluates the model
def generate_models_and_plots():
    print('Training new model...')

    # Generate DataFrame
    df = load_preprocess_dataset()

    # Data and labels for k-fold cross-validation
    k_fold_data = df['cleaned-text']
    k_fold_labels = df['classification']

    # Split the dataset into training and testing sets
    train_data, test_data, train_labels, test_labels = train_test_split(
        df['cleaned-text'], df['classification'], test_size=0.2, random_state=22)

    # Create a TF-IDF vectorizer
    tfidf_vectorizer = TfidfVectorizer(max_features=7000)

    # Transform the text data into TF-IDF features
    k_fold_features = tfidf_vectorizer.fit_transform(k_fold_data)
    train_features = tfidf_vectorizer.transform(train_data)
    test_features = tfidf_vectorizer.transform(test_data)

    # Train a Logistic Regression model
    logistic_regression_model = LogisticRegression(max_iter=1500, random_state=22)
    logistic_regression_model.fit(train_features, train_labels)

    # Backup model if it exists
    if os.path.exists(LOGISTIC_REGRESSION_MODEL_PATH):
        os.replace(LOGISTIC_REGRESSION_MODEL_PATH, LOGISTIC_REGRESSION_MODEL_PATH + ".bak")

    # Backup vectorizer if it exists
    if os.path.exists(TFIDF_VECTORIZER_PATH):
        os.replace(TFIDF_VECTORIZER_PATH, TFIDF_VECTORIZER_PATH + ".bak")

    # Save the TF-IDF vectorizer and trained Logistic Regression model
    joblib.dump(tfidf_vectorizer, TFIDF_VECTORIZER_PATH)
    joblib.dump(logistic_regression_model, LOGISTIC_REGRESSION_MODEL_PATH)

    # Make predictions on the test set
    y_pred = logistic_regression_model.predict(test_features)

    # Evaluate and print model accuracy based on train_test_split
    accuracy = accuracy_score(test_labels, y_pred)
    print(f'\nModel accuracy (train_test_split): {accuracy:.2f}')

    # Generate and print classification report
    class_labels = ['non-suicidal', 'suicidal']
    model_assessment = classification_report(test_labels, y_pred, target_names=class_labels)
    print('\nModel Assessment Report:\n', model_assessment)

    # Perform k-fold cross-validation to assess generalization
    scores = cross_val_score(logistic_regression_model, k_fold_features, k_fold_labels, cv=5)
    print("Cross-Validation Scores:", [f'{score:.2f}' for score in scores])
    print(f'Mean Cross-Validation Accuracy: {scores.mean():.2f}')

    # Generate confusion matrix
    cm = confusion_matrix(test_labels, y_pred)

    print('Model training complete.')

    # Generate data plots
    generate_data_plots(cm, df)


# Classifies the given transcript using the given vectorizer and logistic regression model
# Returns prediction result and writes results to respective CSVs
def analyze_transcript(input_text, tfidf_vectorizer, logistic_regression_model):
    # Split input_text into sections based on consecutive blank lines
    sections = re.split(r'\n\s*\n', input_text)

    # Create a list to store results
    positive_results = []
    negative_results = []

    # Variable to concatenate consecutive client sections
    # This is needed due to transcript formatting: Some client responses are incorrectly split into separate sections.
    concatenated_section = ""

    # Iterate through each section
    for section in sections:
        if "Client:" in section:
            # Extract only the important text for prediction
            section_text = section.split("Client: ")[-1].strip()
            concatenated_section += " " + section_text
        else:
            if concatenated_section:
                # Clean the text
                cleaned_section = clean_text(concatenated_section)

                # Transform the section into TF-IDF features
                section_features = tfidf_vectorizer.transform([cleaned_section])

                # Use the logistic regression model to predict the section classification
                prediction = logistic_regression_model.predict(section_features)[0]

                # Update the suicidal_flag if any section is suicidal
                if prediction == 1:
                    print(f'\nThe following text indicates possible suicidality: \n{concatenated_section}')
                    positive_results.append({'Text': concatenated_section, 'Prediction': 'suicide'})
                else:
                    negative_results.append({'Text': concatenated_section, 'Prediction': 'non-suicide'})

                concatenated_section = ""
            else:
                continue

    # Define the overall result text based on the suicidal_flag
    result_text = "Possible risk of self-harm" if positive_results else "No detected risk of self-harm"

    # Write results to CSV
    csv_columns = ['Text', 'Prediction']
    with open('data/positive_result_dataset.csv', 'a', newline='', encoding='utf-8') as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=csv_columns)

        for data in positive_results:
            writer.writerow(data)

    with open('data/negative_result_dataset.csv', 'a', newline='', encoding='utf-8') as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=csv_columns)

        for data in negative_results:
            writer.writerow(data)

    # Return the overall result text
    return result_text


# Classifies the given input text using the given vectorizer and logistic regression model
# Returns prediction result and writes results to respective CSVs
def classify_simple_text(input_text, tfidf_vectorizer, logistic_regression_model):
    cleaned_text = clean_text(input_text)
    text_features = tfidf_vectorizer.transform([cleaned_text])
    prediction = logistic_regression_model.predict(text_features)[0]

    if prediction == 1:
        print(f'The following text indicates possible suicidality: \n{input_text}')
        result_text = "Possible risk of self-harm"
    else:
        result_text = "No detected risk of self-harm"

    return result_text


# Displays the previously generated data plots or generates new plots if necessary.
def explore_data(app):
    if not os.path.exists(DATA_PLOTS_IMAGE_PATH):
        # Generate and save the plots only if not already generated
        print("Existing data plots not found, generating new plots...")
        generate_models_and_plots()

    # Open a new window to display the saved image
    exploration_window = tk.Toplevel()
    exploration_window.title('Data Exploration Plots')
    exploration_window.grab_set()

    # Get the screen width and height
    screen_width = exploration_window.winfo_screenwidth()
    screen_height = exploration_window.winfo_screenheight()

    # Calculate the x and y coordinates for the window to be centered
    x_coordinate = (screen_width - 1200) // 2
    y_coordinate = (screen_height - 850) // 2
    exploration_window.geometry(f"{1200}x{850}+{x_coordinate}+{y_coordinate}")

    # Load and display the saved image
    data_img = tk.PhotoImage(file=DATA_PLOTS_IMAGE_PATH)
    label = tk.Label(exploration_window, image=data_img)
    label.image = data_img
    label.pack()

    # Function to open a URL when the button is clicked
    def open_url():
        webbrowser.open('https://www.kaggle.com/datasets/nikhileswarkomati/suicide-watch')

    # Create and pack the button
    kaggle_button = tk.Button(exploration_window, text='View Original Kaggle Dataset', command=open_url)
    kaggle_button.pack(side=tk.LEFT, anchor=tk.SW, padx=10, pady=10)

    def update_dataset():
        positive_result_file_path = 'data/positive_result_dataset.csv'
        negative_result_file_path = 'data/negative_result_dataset.csv'

        try:
            # Create a backup copy of the original dataset.csv
            backup_file_path = DATASET_FILE_PATH + '.backup'
            shutil.copyfile(DATASET_FILE_PATH, backup_file_path)

            # Check if result datasets exist
            if os.path.exists(positive_result_file_path) or os.path.exists(negative_result_file_path):
                # Append data from positive_result_dataset.csv to dataset.csv
                if os.path.exists(positive_result_file_path):
                    with open(DATASET_FILE_PATH, 'a', newline='', encoding='utf-8') as dataset_file:
                        with open(positive_result_file_path, 'r', newline='', encoding='utf-8') as positive_result_file:
                            dataset_file.write(positive_result_file.read())

                # Append data from negative_result_dataset.csv to dataset.csv
                if os.path.exists(negative_result_file_path):
                    with open(DATASET_FILE_PATH, 'a', newline='', encoding='utf-8') as dataset_file:
                        with open(negative_result_file_path, 'r', newline='', encoding='utf-8') as negative_result_file:
                            dataset_file.write(negative_result_file.read())

                # Delete processed datasets
                if os.path.exists(positive_result_file_path):
                    os.remove(positive_result_file_path)
                if os.path.exists(negative_result_file_path):
                    os.remove(negative_result_file_path)

                # Display confirmation message in a popup window
                root = tk.Tk()
                root.withdraw()  # Hide the main window
                messagebox.showinfo("Success",
                                    "Data appended successfully to dataset.csv, and result datasets have been deleted.")
            else:
                messagebox.showinfo("Error", "No new results found. Analyze transcripts before updating dataset.")
        except Exception as e:
            print(f"An error occurred: {str(e)}")

    def display_warning_and_update():
        confirmation = messagebox.askokcancel("Warning",
                                              "Are you sure you want to update the dataset? Please review the "
                                              "positive and negative datasets in the data directory before proceeding.",
                                              parent=exploration_window)
        if confirmation:
            update_dataset()

    dataset_update_button = tk.Button(exploration_window, text='Update Dataset', command=display_warning_and_update)
    dataset_update_button.pack(side=tk.RIGHT, anchor=tk.SE, padx=10, pady=10)

    def display_warning_and_retrain():
        app.update_label("Please wait while model is trained...")
        confirmation = messagebox.askokcancel("Warning",
                                              "Are you sure you want to retrain the machine learning model on "
                                              "the current dataset?", parent=exploration_window)

        if confirmation:
            # Close exploration window
            exploration_window.destroy()

            generate_models_and_plots()

            app.update_label("Model training complete.")

        else:
            app.update_label('')

    model_train_button = tk.Button(exploration_window, text='Retrain Model', command=display_warning_and_retrain)
    model_train_button.pack(side=tk.RIGHT, anchor=tk.SE, padx=10, pady=10)


# Parses given transcript text file to populate form fields
def populate_fields(text):
    # Patterns for Clinician ID, Client ID, Session Date, and Text Body
    clinician_pattern = re.compile(r"Clinician ID: (\d+)")
    client_pattern = re.compile(r"Client ID: (\d+)")
    date_pattern = re.compile(r"Session Date: (\d{4}-\d{2}-\d{2})")
    text_body_pattern = re.compile(r"Session Transcript:(.*?)$", re.DOTALL)

    # Search for patterns in the text
    clinician_match = clinician_pattern.search(text)
    client_match = client_pattern.search(text)
    date_match = date_pattern.search(text)
    text_body_match = text_body_pattern.search(text)

    # Extract values from matches or return None if not found
    clinician_id = clinician_match.group(1) if clinician_match else None
    client_id = client_match.group(1) if client_match else None
    session_date = date_match.group(1) if date_match else None
    text_body = text_body_match.group(1).strip() if text_body_match else None

    if not clinician_id or not client_id or not session_date or not text_body:
        return "", "", "", ""

    return clinician_id, client_id, session_date, text_body


# Main screen for user interaction. Displays text input box and interactive buttons.
class TranscriptAnalyzer:
    def __init__(self, root, tfidf_vectorizer, logistic_regression_model, user_mode):
        self.master = root
        root.title("Transcript Analysis Tool")

        # Center the app window
        screen_width = self.master.winfo_screenwidth()
        screen_height = self.master.winfo_screenheight()

        x_coordinate = (screen_width - 730) // 2
        y_coordinate = (screen_height - 425) // 2

        root.geometry(f"{730}x{425}+{x_coordinate}+{y_coordinate}")

        # File selection button
        self.select_file = tk.Button(root, text="Load Transcript", command=self.load_transcript)
        self.select_file.place(x=10, y=385)

        # Scrolled Text Input Box
        default_text = "Enter text or select a text file to analyze."
        self.text_entry = ScrolledText(root, height=20, width=68)
        self.text_entry.place(x=10, y=45)
        self.text_entry.insert(tk.END, default_text)
        self.text_entry.tag_configure("grey")
        self.text_entry.tag_add("grey", "1.0", "end")
        self.text_entry.bind("<FocusIn>", self.on_entry_focus)

        # Clinician ID label and entry
        self.clinician_id_var = tk.StringVar()
        self.clinician_id_label = tk.Label(root, text="Clinician ID:")
        self.clinician_id_label.place(x=580, y=40)
        self.clinician_id_entry = tk.Entry(root, textvariable=self.clinician_id_var, state="readonly")
        self.clinician_id_entry.place(x=590, y=65)

        # Client ID label and entry
        self.client_id_var = tk.StringVar()
        self.client_id_label = tk.Label(root, text="Client ID:")
        self.client_id_label.place(x=580, y=95)
        self.client_id_entry = tk.Entry(root, textvariable=self.client_id_var, state="readonly")
        self.client_id_entry.place(x=590, y=120)

        # Session Date label and entry
        self.session_date_var = tk.StringVar()
        self.session_date_label = tk.Label(root, text="Session Date:")
        self.session_date_label.place(x=580, y=150)
        self.session_date_entry = tk.Entry(root, textvariable=self.session_date_var, state="readonly")
        self.session_date_entry.place(x=590, y=175)

        # Simple Analysis Button
        self.analyze_button = tk.Button(root, text="Simple Text Analysis", command=self.simple_text_classify)
        self.analyze_button.place(x=10, y=10)

        # Transcript Analysis Button
        self.export_button = tk.Button(root, text="Analyze Current Transcript", command=self.analyze_and_export)
        self.export_button.place(x=110, y=385)

        # Analyze All Button
        self.analyze_all_button = tk.Button(root, text="Analyze ALL Transcripts", command=self.analyze_all_transcripts)
        self.analyze_all_button.place(x=270, y=385)

        if user_mode == "admin":
            # Explore Data Button
            self.explore_data_button = tk.Button(root, text="Model and Data Tools",
                                                 command=lambda: explore_data(self))
            self.explore_data_button.place(x=590, y=385)

        # Result Label
        self.result_label = tk.Label(root, text="")
        self.result_label.place(x=150, y=15)

        # Models
        self.tfidf_vectorizer = tfidf_vectorizer
        self.logistic_regression_model = logistic_regression_model

    # Updates Info Label
    def update_label(self, text):
        self.result_label.config(text=text)

    # Clears main text field when initially focused
    def on_entry_focus(self, event):
        # When the entry gets focus, check if it contains the default text
        current_text = self.text_entry.get("1.0", "end-1c")
        default_text = "Enter text or select a text file to analyze."

        if current_text == default_text:
            # If it does, delete the default text
            self.text_entry.delete("1.0", tk.END)
            # Remove the grey tag
            self.text_entry.tag_remove("grey", "1.0", tk.END)

    # Analyzes text currently in main text field
    def simple_text_classify(self):
        # Get input text from the text entry box
        input_text = self.text_entry.get("1.0", tk.END).strip()

        if input_text == "" or input_text == "Enter text or select a text file to analyze.":
            self.result_label.config(text="Please enter text for analysis.")
        else:
            # Classify the input text
            result_text = classify_simple_text(input_text, self.tfidf_vectorizer, self.logistic_regression_model)

            # Display the result
            self.update_label(f"Analysis Result: {result_text}")

    def transcript_classify(self):
        # Get input text from the text entry box
        input_text = self.text_entry.get("1.0", tk.END).strip()

        if input_text == "" or input_text == "Enter text or select a text file to analyze.":
            self.result_label.config(text="Please enter text for analysis.")
        else:
            # Classify the input text
            result_text = analyze_transcript(input_text, self.tfidf_vectorizer, self.logistic_regression_model)

            # Display the result
            self.update_label(f"Analysis Result: {result_text}")

    # Prompts user to select a transcript text file, populates form fields with transcript content
    def load_transcript(self):
        file_path = filedialog.askopenfilename(initialdir=TRANSCRIPT_DIRECTORY, filetypes=[("Text Files", "*.txt")])

        if file_path:
            with open(file_path, "r") as file:
                session_transcript = file.read()

                # Extract information from the text
                clinician_id, client_id, session_date, text_body = populate_fields(session_transcript)

                self.text_entry.delete("1.0", tk.END)
                self.text_entry.insert(tk.END, text_body)

                # Populate the new fields
                self.clinician_id_var.set(clinician_id)
                self.client_id_var.set(client_id)
                self.session_date_var.set(session_date)

    # Analyzes the currently loaded transcript and saves results as a text file in the output directory
    def analyze_and_export(self):
        client_id = self.client_id_entry.get()
        clinician_id = self.clinician_id_entry.get()
        session_date = self.session_date_entry.get()

        proceed = True

        # Check if any variable is empty
        if not client_id or not clinician_id or not session_date:
            messagebox.showwarning("Warning", "Client ID, Clinician ID, and Session Date are required "
                                              "for transcript analysis. Load a transcript to use this function.")
            return

        filename = f"result_{client_id}_{session_date}.txt"
        file_path = os.path.join("output", filename)

        if os.path.exists(file_path):
            confirmation = messagebox.askyesno("Warning", f'Transcript for client {client_id} on '
                                                          f'{session_date} has already been analyzed. Would you like to '
                                                          f'analyze again?')
            if not confirmation:
                proceed = False

        if proceed:
            self.transcript_classify()
            result = self.result_label.cget("text")

            with open(file_path, "w") as file:
                file.write(f"Client ID: {client_id}\n")
                file.write(f"Clinician ID: {clinician_id}\n")
                file.write(f"Session Date: {session_date}\n")
                file.write(f"{result}")

    # Handles analyze all transcripts action
    # Iterates through all txt files in transcripts directory and calls process_transcript() for each file
    def analyze_all_transcripts(self):
        # Check if there are any .txt files in the directory
        transcripts_exist = any(filename.endswith(".txt") for filename in os.listdir(TRANSCRIPT_DIRECTORY))

        if transcripts_exist:
            # Iterate through all files in the directory
            for filename in os.listdir(TRANSCRIPT_DIRECTORY):
                if filename.endswith(".txt"):
                    # Construct the full file path
                    file_path = os.path.join(TRANSCRIPT_DIRECTORY, filename)

                    # Process the current transcript file
                    self.process_transcript(file_path)

            # Update Label
            self.update_label("All transcripts processed.")
        else:
            # Update Label when no transcripts are found
            self.update_label("No transcripts found.")

    # Loads the given transcript and calls analyze_and_export() for each
    def process_transcript(self, file_path):
        with open(file_path, "r") as file:
            session_transcript = file.read()

            # Extract information from the text
            clinician_id, client_id, session_date, text_body = populate_fields(session_transcript)

            # Set the extracted information to the corresponding fields
            self.text_entry.delete("1.0", tk.END)
            self.text_entry.insert(tk.END, text_body)
            self.clinician_id_var.set(clinician_id)
            self.client_id_var.set(client_id)
            self.session_date_var.set(session_date)

            # Export the result to a file
            self.analyze_and_export()

            # Clear fields
            self.text_entry.delete("1.0", tk.END)
            self.clinician_id_var.set('')
            self.client_id_var.set('')
            self.session_date_var.set('')


# User mode selection
def mode_selection():
    root = tk.Tk()
    root.title("Transcript Analysis Tool Login")

    # Center the app window
    screen_width = root.winfo_screenwidth()
    screen_height = root.winfo_screenheight()

    x_coordinate = (screen_width - 400) // 2
    y_coordinate = (screen_height - 200) // 2

    root.geometry(f"{400}x{200}+{x_coordinate}+{y_coordinate}")

    def login():
        mode = mode_var.get()
        if mode == "Clinician":
            user_mode.set("user")
            root.destroy()
        elif mode == "Admin":
            password = simpledialog.askstring("Admin Login", "Enter the admin password:", show='*')
            if password == ADMIN_PASSWORD:
                user_mode.set("admin")
                root.destroy()
            else:
                messagebox.showerror("Error", "Incorrect password. Proceeding with standard user access.")
                mode_var.set("Clinician")  # Reset mode to Clinician
        else:
            messagebox.showerror("Error", "Invalid mode selection.")
            mode_var.set("Clinician")  # Reset mode to Clinician

    mode_var = tk.StringVar(value="Clinician")
    modes = ["Clinician", "Admin"]

    mode_label = tk.Label(root, text="Select user mode:")
    mode_label.place(x=50, y=50)

    mode_menu = tk.OptionMenu(root, mode_var, *modes)
    mode_menu.place(x=155, y=45)

    login_button = tk.Button(root, text="Login", command=login)
    login_button.place(x=125, y=110)

    cancel_button = tk.Button(root, text="Cancel", command=root.destroy)
    cancel_button.place(x=225, y=110)

    user_mode = tk.StringVar(value="")  # Variable to store the selected mode

    root.mainloop()
    return user_mode.get()  # Return the selected mode after the window is closed


def main():
    # Load existing models
    tfidf_vectorizer, logistic_regression_model = load_models()

    # Regenerate models if they do not exist
    if tfidf_vectorizer is None or logistic_regression_model is None:
        print("Existing model not found.")
        generate_models_and_plots()
        tfidf_vectorizer, logistic_regression_model = load_models()
        print("\nAll pre-launch tasks completed.")

    # User Mode Selection
    user_mode = mode_selection()

    if user_mode:
        # Create Tkinter application
        root = tk.Tk()
        app = TranscriptAnalyzer(root, tfidf_vectorizer, logistic_regression_model, user_mode)
        root.mainloop()
        print("Closing app...")


if __name__ == '__main__':
    main()
