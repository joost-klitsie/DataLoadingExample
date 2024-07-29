This is example code to showcase a reactive aproach to load data into a View Model.

The full article can be found on [Medium](https://medium.com/@joostklitsie/the-best-way-to-load-data-in-viewmodels-a112ced54e07) 

If you run the project, you will see a screen that can:

1. Load data from memory directly if it is there
2. Load data from another source if data is not available in memory
3. Offer retry mechanism if data fails to load
4. After data successfully is loaded, updates to the data will populate the screen
5. You can refresh data by pushing a button
6. If refreshing data fails, you are informed.

In the example project loading of data is randomized, giving you a 50/50 chance of successfully loading data.
In the following GIF's you will see the behavior described above:

![loaded_at_start-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/1c738784-915e-4a6c-8a59-3b3b6e82c39c) ![loading_success-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/c59df5a1-82cf-404d-ab9d-4d4703e68b7f) ![loading_with_failure-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/04669360-2a79-4097-b8a8-d241549c22af)
