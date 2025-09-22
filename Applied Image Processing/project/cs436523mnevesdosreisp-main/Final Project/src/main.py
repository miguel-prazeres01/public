from tkinter import *
from tkinter import filedialog, ttk, colorchooser
from PIL import Image, ImageTk
import os

from draw import *

if __name__ == '__main__':

    #Making sure the environment is ready
    if os.environ.get('DISPLAY','') == '':
        os.environ.__setitem__('DISPLAY', ':0.0')

    #Creating new Tkinter window
    window = Tk()

    window.title("Final Project")
    window.resizable(width=True, height=True)
    window.geometry("1920x1080")
    
    #Call object draw that performs transformations to the class
    draw(window)

    window.mainloop()