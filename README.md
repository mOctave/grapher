# Grapher

This program is a systematic program deisgn project I undertook between May 2024 and March 2025. As with everything I do, I overcomplicated the whole thing, but I believe it's fully functional and it has a couple of cool features that might come in handy for you.

## Installation‬
The grapher program ships as a .jar file, to be opened by the Java Virtual‬ Machine, built using Java 21. In order to run it, you will need to install JDK 21, which‬ you can find at‬‭ https://www.oracle.com/java/technologies/downloads/#java21‬‭. Download‬ it and follow all instructions. Once you have the JDK installed, you should be able to‬ double-click the .jar file to open the program. If you cannot, please contact me for help.‬

## Introduction to Use‬
When you open the program, you will be met with three windows: one showing‬ an empty table, one with a button saying “Add Dataset,” and one with a blank graph.‬
‭

### Data Table‬
‭The window with the empty table is the program’s main window. Here, you can‬ add data and save or load projects (using the Project menu in the top left). The best‬ place to start is generally by importing CSV which you’ve exported from a spreadsheet‬ program. You can do this by using Data→Import CSV in the top menu, which will open a‬ file picker.‬

You can also enter data manually. To do so, click the cell or column header you‬ want to change, and enter the data. If you have a cell selected, you can insert new‬ columns or rows using the plus buttons surrounding your selected cell. If you need to‬ delete a row or column, you can do so from the Data menu. Unfortunately, there’s no‬ undo button, so be careful not to delete anything important!‬
‭

Once you have data, you can sort it using Data→Sort by Selected Column. This‬ will sort the entire table in ascending order according to the data in the column (ignoring‬ the header). You can also access statistics on the bottom panel for the column you have‬ selected.‬
‭

Finally, you can search your data using Data→Search or the magnifying glass in‬ the top left hand corner. This will open a dialog. Enter your search term, and every‬ matching cell will be highlighted (you can jump between matches using the double‬ arrow under the magnifying glass). When you’re done, click the X beneath the‬ magnifying glass to close the search.‬
‭

### Plotting Data‬
Once you have your data ready to plot, you can switch to windows, and use the‬ giant “Add Dataset” button to start plotting data. Optionally give your data a name, and‬ then select series to use for the X and Y axes. You can also include error bars in the‬ same way, or a trendline. If you want to change the colour that the dataset is plotted in,‬ click the colourful box at the right edge of the window. You can delete datasets with the‬ large X button.‬
‭

After you’ve added valid values for the X and Y axes, the data should be plotted‬ automatically on the graph! Using the graph window, you can change the title of the‬ graph as well as its two axis titles, and switch between scatterplot and line graph‬ modes. Once you have a graph you are happy with, resize the window to the size you‬ want and then hit “Export” to get a .png file.‬

‭
### Gridlines
‭
Unfortunately, it was not feasible to implement automatic gridlines as part of the‬ program in the end. Thus, gridlines remain the hardest to use part of this program. If the‬ default window (gridlines at x=0 and y=0, bounds at ±10) do not work for you, then you‬ will have to add custom gridlines.‬
‭

To do so, add a column to your data table, and title it however you want (I usually‬ call mine “Gridlines (X)” and “Gridlines (Y)”). Assign this as a gridline series using one of‬ the dropdown menus on the graph window. Note that the labels on the graph window‬ might be misleading; horizontal gridlines are drawn vertically at specific points on the‬ X-axis, not horizontal lines at specific points on the Y-axis. You should notice that one of‬ the lines on the graph immediately vanishes. You can now enter custom gridline data in‬ your new column, and produce your own gridlines. Gridline data is formatted as follows:‬

- The first cell in the column is treated as the lower (or left) bound. Any numbers lower than this will not be plotted.‬
- The second cell is treated as the upper (or right) bound. Any numbers higher‬ than this will not be plotted.‬
- Any subsequent values are used as X or Y values to draw gridlines on.‬ While it is vitally important to include values in the first two cells of your column,‬ all gridlines are purely aesthetic and you can have as many or as few as you’d like.‬

### Saving and Loading Data‬
If you have created a project and want to save it, you can do so using‬ Project→Save As in the data table window. It will prompt you to choose a file name.‬ Once you’ve used “Save As” once, the project will automatically save all other changes‬ you make, and you do not need to use it again. When you are done with your project,‬ you can close the program without saving.‬ You can load a previously saved project using Project→Open. Select your project‬ and it will load all your data, including graph titles. It will also keep the project open and‬ continue autosaving to it: you do not need to use “Save As” to save the project after‬ you’ve opened it.‬
‭
‭
‭
