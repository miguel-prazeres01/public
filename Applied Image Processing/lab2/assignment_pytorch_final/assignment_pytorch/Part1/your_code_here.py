import torch
import torch.optim as optim

from helper_functions import *

def normalize(img, mean, std):
    """ Normalizes an image tensor.

    # Parameters:
        @img, torch.tensor of size (b, c, h, w)
        @mean, torch.tensor of size (c)
        @std, torch.tensor of size (c)

    # Returns the normalized image
    """
    # TODO: 1. Implement normalization doing channel-wise z-score normalization.
    z_score = transforms.Normalize(mean,std)

    out = z_score(img)

    return (out) 

def content_loss(input_features, content_features, content_layers):
    """ Calculates the content loss as in Gatys et al. 2016.

    # Parameters:
        @input_features, VGG features of the image to be optimized. It is a 
            dictionary containing the layer names as keys and the corresponding 
            features volumes as values.
        @content_features, VGG features of the content image. It is a dictionary 
            containing the layer names as keys and the corresponding features 
            volumes as values.
        @content_layers, a list containing which layers to consider for calculating
            the content loss.
    
    # Returns the content loss, a torch.tensor of size (1)
    """
    # TODO: 2. Implement the content loss given the input feature volume and the
    # content feature volume. Note that:
    # - Only the layers given in content_layers should be used for calculating this loss.
    # - Normalize the loss by the number of layers.

    loss = nn.MSELoss()

    out = 0

    for layer in content_layers:
        out += loss(input_features[layer], content_features[layer])
    out = out / len(content_layers)

    return out # Initialize placeholder such that the code runs

def gram_matrix(x):
    """ Calculates the gram matrix for a given feature matrix.

    # NOTE: Normalize by number of number of dimensions of the feature matrix.
    
    # Parameters:
        @x, torch.tensor of size (b, c, h, w) 

    # Returns the gram matrix
    """
    # TODO: 3.2 Implement the calculation of the normalized gram matrix. 
    # Do not use for-loops, make use of Pytorch functionalities.

    _ , c , h , w = x.size()

    x = x.view(c, h * w)

    out = torch.matmul(x, x.t())

    out = out/ (c * h * w)

    return out

def style_loss(input_features, style_features, style_layers):
    """ Calculates the style loss as in Gatys et al. 2016.

    # Parameters:
        @input_features, VGG features of the image to be optimized. It is a 
            dictionary containing the layer names as keys and the corresponding 
            features volumes as values.
        @style_features, VGG features of the style image. It is a dictionary 
            containing the layer names as keys and the corresponding features 
            volumes as values.
        @style_layers, a list containing which layers to consider for calculating
            the style loss.
    
    # Returns the style loss, a torch.tensor of size (1)
    """
    # TODO: 3.1 Implement the style loss given the input feature volume and the
    # style feature volume. Note that:
    # - Only the layers given in style_layers should be used for calculating this loss.
    # - Normalize the loss by the number of layers.
    # - Implement the gram_matrix function.

    loss = nn.MSELoss()

    out = 0

    for layer in style_layers:
        out += loss(gram_matrix(input_features[layer]), gram_matrix(style_features[layer]))

    out = out / len(style_features)

    return out # Initialize placeholder such that the code runs

    #return torch.rand((1), requires_grad=True) # Initialize placeholder such that the code runs

def total_variation_loss(y):
    """ Calculates the total variation across the spatial dimensions.

    # Parameters:
        @x, torch.tensor of size (b, c, h, w)
    
    # Returns the total variation, a torch.tensor of size (1)
    """

    _ , c , K , J = y.size()

    sumK = 0
    sumJ = 0
    out = 0

    sumK = torch.sum(abs(torch.diff(y,dim=2)))
    sumJ = torch.sum(abs(torch.diff(y,dim=3)))

    out = (sumK + sumJ) / (c * K * J)

    # TODO: 4. Implement the total variation loss. Normalize by tensor dimension sizes

    #return torch.rand((1), requires_grad=True)

    return out# Initialize placeholder such that the code runs

def run_single_image(vgg_mean, vgg_std, content_img, style_img, num_steps, random_init, w_style, w_content, w_tv,content_layers, style_layers, device):
    """ Neural Style Transfer optmization procedure for a single style image.
    
    # Parameters:
        @vgg_mean, VGG channel-wise mean, torch.tensor of size (c)
        @vgg_std, VGG channel-wise standard deviation, detorch.tensor of size (c)
        @content_img, torch.tensor of size (1, c, h, w)
        @style_img, torch.tensor of size (1, c, h, w)
        @num_steps, int, iteration steps
        @random_init, bool, whether to start optimizing with based on a random image. If false,
            the content image is as initialization.
        @w_style, float, weight for style loss
        @w_content, float, weight for content loss 
        @w_tv, float, weight for total variation loss

    # Returns the style-transferred image
    """

    # Initialize Model
    model = Vgg19(content_layers, style_layers, device)

    # TODO: 1. Normalize Input images
    normed_style_img = normalize(style_img, vgg_mean, vgg_std)
    normed_content_img = normalize(content_img, vgg_mean, vgg_std)

    # Retrieve feature maps for content and style image
    # We do not need to calculate gradients for these feature maps
    with torch.no_grad():
        style_features = model(normed_style_img)
        content_features = model(normed_content_img)
    
    # Either initialize the image from random noise or from the content image
    if random_init:
        optim_img = torch.randn(content_img.data.size(), device=device)
        optim_img = torch.nn.Parameter(optim_img, requires_grad=True)
    else:
        optim_img = torch.nn.Parameter(content_img.clone(), requires_grad=True)

    # Initialize optimizer and set image as parameter to be optimized
    optimizer = optim.LBFGS([optim_img])
    
    # Training Loop
    iter = [0]
    while iter[0] <= num_steps:

        def closure():
            
            # Set gradients to zero before next optimization step
            optimizer.zero_grad()

            # Clamp image to lie in correct range
            with torch.no_grad():
                optim_img.clamp_(0, 1)

            # Retrieve features of image that is being optimized
            normed_img = normalize(optim_img, vgg_mean, vgg_std)
            input_features = model(normed_img)

            # TODO: 2. Calculate the content loss
            if w_content > 0:
                c_loss = w_content * content_loss(input_features, content_features, content_layers)
            else: 
                c_loss = 0

            # TODO: 3. Calculate the style loss
            if w_style > 0:
                s_loss = w_style * style_loss(input_features, style_features, style_layers)
            else:
                s_loss = 0

            # TODO: 4. Calculate the total variation loss
            if w_tv > 0:
                tv_loss = w_tv * total_variation_loss(normed_img)
            else:
                tv_loss = 0

            # Sum up the losses and do a backward pass
            loss = s_loss + c_loss + tv_loss 
            loss.backward()

            # Print losses every 50 iterations
            iter[0] += 1
            if iter[0] % 50 == 0:
                print('iter {}: | Style Loss: {:4f} | Content Loss: {:4f} | TV Loss: {:4f}'.format(
                    iter[0], s_loss.item(), c_loss.item(), tv_loss.item()))

            return loss

        # Do an optimization step as defined in our closure() function
        optimizer.step(closure)
    
    # Final clamping
    with torch.no_grad():
        optim_img.clamp_(0, 1)

    return optim_img

def run_double_image(
    vgg_mean, vgg_std, content_img, style_img_1, style_img_2, num_steps, 
    random_init, w_style_1, w_style_2, w_content, w_tv, content_layers, style_layers, device):


    model = Vgg19(content_layers, style_layers, device)

    # TODO: 1. Normalize Input images
    normed_style_img_1 = normalize(style_img_1, vgg_mean, vgg_std)
    normed_style_img_2 = normalize(style_img_2, vgg_mean, vgg_std)
    normed_content_img = normalize(content_img, vgg_mean, vgg_std)

    # Retrieve feature maps for content and style image
    # We do not need to calculate gradients for these feature maps
    with torch.no_grad():
        style_features_1 = model(normed_style_img_1)
        style_features_2 = model(normed_style_img_2)
        content_features = model(normed_content_img)
    
    # Either initialize the image from random noise or from the content image
    if random_init:
        optim_img = torch.randn(content_img.data.size(), device=device)
        optim_img = torch.nn.Parameter(optim_img, requires_grad=True)
    else:
        optim_img = torch.nn.Parameter(content_img.clone(), requires_grad=True)

    # Initialize optimizer and set image as parameter to be optimized
    optimizer = optim.LBFGS([optim_img])
    
    # Training Loop
    iter = [0]
    while iter[0] <= num_steps:

        def closure():
            
            # Set gradients to zero before next optimization step
            optimizer.zero_grad()

            # Clamp image to lie in correct range
            with torch.no_grad():
                optim_img.clamp_(0, 1)

            # Retrieve features of image that is being optimized
            normed_img = normalize(optim_img, vgg_mean, vgg_std)
            input_features = model(normed_img)

            # TODO: 2. Calculate the content loss
            if w_content > 0:
                c_loss = w_content * content_loss(input_features, content_features, content_layers)
            else: 
                c_loss = 0

            # TODO: 3. Calculate the style loss
            if w_style_1 > 0 and w_style_2 > 0:
                s_loss_1 = w_style_1 * style_loss(input_features, style_features_1, style_layers)
                s_loss_2 = w_style_2 * style_loss(input_features, style_features_2, style_layers)

                s_loss = (s_loss_1 + s_loss_2)

            else:
                s_loss = 0

            # TODO: 4. Calculate the total variation loss
            if w_tv > 0:
                tv_loss = w_tv * total_variation_loss(normed_img)
            else:
                tv_loss = 0

            # Sum up the losses and do a backward pass
            loss = s_loss + c_loss + tv_loss 
            loss.backward()

            # Print losses every 50 iterations
            iter[0] += 1
            if iter[0] % 50 == 0:
                print('iter {}: | Style Loss: {:4f} | Content Loss: {:4f} | TV Loss: {:4f}'.format(
                    iter[0], s_loss.item(), c_loss.item(), tv_loss.item()))

            return loss

        # Do an optimization step as defined in our closure() function
        optimizer.step(closure)
    
    # Final clamping
    with torch.no_grad():
        optim_img.clamp_(0, 1)

   # TODO: 5. Implement style transfer for two given style images.

    return optim_img
