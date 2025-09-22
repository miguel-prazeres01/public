# Final Project : Depth of field
## Table of contents

- [Quick start](#quick-start)
- [What's included](#whats-included)
- [Basic Algorithm](#basic-algorithm)
- [Implementation details](#implementation-details)
- [Setup](#setup)
- [Thanks](#thanks)
- [Copyright and license](#copyright-and-license)


## Quick start

This is an implementation of the computational depth of field topic. It is based on a paper and article provided by the faculty: 

Paper: [Liao, Jingtang, Shuheng Shen, and Elmar Eisemann: "Depth annotations: Designing depth of a single image for depth-based effects." Computers & Graphics (2018)](https://graphics.tudelft.nl/Publications-new/2017/LSE17a/depthannotations-authorsversion.pdf)

Article: [Chapter 28. Practical Post-Process Depth of Field](https://developer.nvidia.com/gpugems/gpugems3/part-iv-image-effects/chapter-28-practical-post-process-depth-field)

The project was developed in both python and c++ and can run on windows or linux.

## What's included

The project is divided in two parts: the sorce code (python and c++) and the pretrained RGB->Depth CNN (MiDaS) 

```text
Final Project/
    ├── MiDaS-master/
    ├── src/  
    │   ├── c++/
    │   ├── examples/
    │   ├── main.py
    │   ├── draw.py
    └── environment.yalm 
```

## Basic Algorithm
1. [Load an RGB from disk.](https://gitlab.ewi.tudelft.nl/cgv/cs4365/student-repositories/2023-2024/cs436523mnevesdosreisp/-/blame/main/Final%20Project/src/draw.py#L33) 
2. [Allow users to scribble depth annotations in UI.](https://gitlab.ewi.tudelft.nl/cgv/cs4365/student-repositories/2023-2024/cs436523mnevesdosreisp/-/blame/main/Final%20Project/src/draw.py#L65)
3. [Diffuse annotations across the image using Possion image editing.](https://gitlab.ewi.tudelft.nl/cgv/cs4365/student-repositories/2023-2024/cs436523mnevesdosreisp/-/blame/main/Final%20Project/src/c++/src/your_code_here.h?ref_type=heads#L92)
4. [Allow users to select focus depth and aperture size.](https://gitlab.ewi.tudelft.nl/cgv/cs4365/student-repositories/2023-2024/cs436523mnevesdosreisp/-/blame/main/Final%20Project/src/draw.py#L116)
5. [Simulate depth-of-field using a spatially varying cross-bilateral filter.](https://gitlab.ewi.tudelft.nl/cgv/cs4365/student-repositories/2023-2024/cs436523mnevesdosreisp/-/blame/main/Final%20Project/src/c++/src/your_code_here.h#L188)
6. [Save and display the result.](https://gitlab.ewi.tudelft.nl/cgv/cs4365/student-repositories/2023-2024/cs436523mnevesdosreisp/-/blame/main/Final%20Project/src/draw.py#L53)

Bonus:

7. [Use a pretrained RGB->Depth CNN to supplement the depth.](https://gitlab.ewi.tudelft.nl/cgv/cs4365/student-repositories/2023-2024/cs436523mnevesdosreisp/-/blame/main/Final%20Project/src/draw.py#L135)

## Implentation details
1. The initial image is loaded using python.
2. The scribble and source images are saved in the c++ inputs folder.
3. The c++ code implements the basic algorithm.
4. The final image is then loaded into python again from the output folder in c++.

Bonus:
The CNN is implemented and called in python.

Outputs:
In the output file of c++ all the intermediate steps are shown in different images.

## Setup

1. Open Visual Studio in the c++ folder.

2. Build all the dependencies in **x64-release** configuration.

3. Go to MiDaS website and download the **dpt_beit_large_512** [weight](https://github.com/isl-org/MiDaS/releases/download/v3_1/dpt_beit_large_512.pt) (or click the link) and put it in the [weights](https://gitlab.ewi.tudelft.nl/cgv/cs4365/student-repositories/2023-2024/cs436523mnevesdosreisp/-/tree/main/Final%20Project/MiDaS-master/weights) folder.

4. Open Anaconda Prompt in the project folder.

5. Configure Anaconda environment: 

> + conda env create -f environment.yaml

> + conda activate projectAIP

## How to run

1. Go to the src directory.
2. Run: 
> + python main.py
3. Choose an image from the examples directory.
4. Draw **scribbles** with different color scale.
5. Choose **depth of field** (*integer 0 to 255*) and **aperture size** (*integer 1 to 10*).
6. Choose the mode of calculating depth (**Diffusion of scribbles** or **CNN**)
7. See the results after the computation.


## GitLab repository

https://gitlab.ewi.tudelft.nl/cgv/cs4365/student-repositories/2023-2024/cs436523mnevesdosreisp

