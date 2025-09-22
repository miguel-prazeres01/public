from tkinter import *
from tkinter import filedialog
from PIL import Image, ImageTk, ImageDraw, ImageOps
import subprocess
import os


def rgbtohex(rgb):
    return f'#{rgb[0]:02x}{rgb[1]:02x}{rgb[2]:02x}'

class draw:
    def __init__(self,master):
        self.master = master
        self.file = None
        self.old_x = None
        self.old_y = None
        self.black_points = []
        self.grey_points = []
        self.penwidth = 5
        self.img = None
        self.color = (0,0,0)
        self.drawWidgets()
        self.create_menu()
        self.create_erasor()
        self.create_depth_button()
        self.create_lables()
        self.create_scale()
        self.create_depth_button_CNN()
        self.c.bind('<B1-Motion>',self.paint)
        self.c.bind('<ButtonRelease-1>',self.reset)
        

    def load_image(self):
        #Loading source image to use
        self.file = filedialog.askopenfilename()
        self.img = Image.open(self.file).convert("RGB")
        #Creating RGBA image to draw the scribbles
        self.white_image = Image.new("RGBA", (self.img.width, self.img.height))
        self.draw = ImageDraw.Draw(self.white_image)

        tk_img = ImageTk.PhotoImage(self.img)

        #Reconfiguring the canvas to fit to the image size
        self.c.configure(width=self.img.width, height=self.img.height)
        self.c.width = self.img.width
        self.c.height = self.img.height

        #Placing the image in the canvas
        self.img_canvas = self.c.create_image(self.c.winfo_width()/2,self.c.winfo_height()/2,image=tk_img, anchor=CENTER)
        self.c.image = tk_img
        

    def save_image(self):
        self.img.save("final_image.png")
        self.c.delete()


    def create_menu(self):
        menu = Menu(self.master)
        menu.add_command(label='Load image',command=self.load_image)
        menu.add_command(label='Save image', command=self.save_image)
        self.master.config(menu=menu)
    
    # Function used to draw the scribbles in the canvas and in the RGBA image
    def paint(self,e):
        if self.old_x and self.old_y:
            # convert rgb to hexadecimal to fit the canvas formatting
            color = rgbtohex(self.color)
            #draw in canvas
            self.c.create_line(self.old_x,self.old_y,e.x,e.y,width=self.penwidth,fill=color,capstyle=ROUND,smooth=True)
            
            # make sure the drawings in the image are in the same place as the canvas
            dist1 = abs(self.c.winfo_width() - self.c.width) //2
            dist2 = abs(self.c.winfo_height() - self.c.height) //2
            
            #draw in image
            if self.draw != None:
                self.draw.line([self.old_x - dist1,self.old_y - dist2,e.x - dist1,e.y - dist2],self.color,4)

        self.old_x = e.x
        self.old_y = e.y

    # reseting or cleaning the canvas 
    def reset(self,e):    
        self.old_x = None
        self.old_y = None      
        
    def drawWidgets(self):
        self.c = Canvas(self.master,width=500,height=400)
        self.c.pack(fill=BOTH,expand=True)
        
    # Function used to erase the scribbles in the canvas and image
    def erase_scribbles(self):
        items = self.c.find_all()
        while len(items) > 1 :
            self.c.delete(items[-1])
            items = items[:-1]
        if self.img != None:
            # Creating new image since it does not allow to erase the scribbles in the RGBA picture
            self.white_image = Image.new("RGBA", (self.img.width, self.img.height))
            self.draw = ImageDraw.Draw(self.white_image)
            
    def create_erasor(self):
        erasor = Button(self.master, text="Erasor",command=self.erase_scribbles, width=6, height=1)
        erasor.place(x=5,y=120)
    

    def create_depth_button(self):
        depth = Button(self.master, text="Calculate depth",command=self.calculate_depth)
        depth.place(x=1788,y=120)

    def create_depth_button_CNN(self):
        depth = Button(self.master, text="Calculate depth CNN",command=self.calculate_depth_CNN)
        depth.place(x=1754,y=150)

    def create_lables(self):
        label1 = Label(self.master, text="Focus Depth")
        label1.place(x=1805,y=0)
        self.focus_depth = Entry(self.master, width=15)
        self.focus_depth.place(x=1785, y=30)
        label2 = Label(self.master, text="Aperture Size")
        label2.place(x=1805,y=60)
        self.aperture_size = Entry(self.master, width=15)
        self.aperture_size.place(x=1785, y=90)

    def create_scale(self):
        v1 = IntVar()
        scale = Scale(self.master,variable=v1, from_=0, to=255,command=self.change_color)
        scale.place(x=10, y=0)

    def change_color(self,value):
        self.color = (int(value),int(value),int(value))
    

    def calculate_depth_CNN(self):
        self.img.save("../MiDaS-master/input/image.png")
        os.chdir("../MiDaS-master")
        # Calling the pretrained CNN
        os.system("python run.py --model_type dpt_beit_large_512 --input_path input --output_path output")
        os.chdir("../src")

        #Saving the depth image
        new_image = Image.open("../MiDaS-master/output/image-dpt_beit_large_512.png")
        new_image = ImageOps.grayscale(new_image)
        new_image.save("c++/data/python/depth.png")
        self.img.save("c++/data/python/image.jpg")

        # Getting the focus depth and aperture size values to call c++ program
        focus_depth = self.focus_depth.get()
        aperture_size = self.aperture_size.get()

        #Calling c++ program
        if focus_depth != '' and aperture_size != '':
            subprocess.check_call(["c++/out/build/x64-Release/a2_warping.exe","1",aperture_size,focus_depth])
            final_image = Image.open("c++/outputs/3_depth_of_field_image.png").convert("RGB")

            tk_img = ImageTk.PhotoImage(final_image)

            #Placing the image in the canvas
            self.img_canvas = self.c.create_image(self.c.winfo_width()/2,self.c.winfo_height()/2,image=tk_img, anchor=CENTER)
            self.c.image = tk_img
            self.img = final_image


    def calculate_depth(self):
        # Saving the images to respective folder
        new_image = self.white_image
        new_image.save("c++/data/python/scribles.png")
        self.img.save("c++/data/python/image.jpg")

        # Getting the focus depth and aperture size values to call c++ program
        focus_depth = self.focus_depth.get()
        aperture_size = self.aperture_size.get()

        #Calling c++ program
        if focus_depth != '' and aperture_size != '':
            subprocess.check_call(["c++/out/build/x64-Release/a2_warping.exe","0",aperture_size,focus_depth])
            final_image = Image.open("c++/outputs/3_depth_of_field_image.png").convert("RGB")

            tk_img = ImageTk.PhotoImage(final_image)

            #Placing the image in the canvas
            self.img_canvas = self.c.create_image(self.c.winfo_width()/2,self.c.winfo_height()/2,image=tk_img, anchor=CENTER)
            self.c.image = tk_img
            self.img = final_image
        
       

